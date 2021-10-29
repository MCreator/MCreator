<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "../mcitems.ftl">
<#include "../triggers.java.ftl">
<#include "../procedures.java.ftl">

package ${package}.item;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ${name}Item extends Item {

	public ${name}Item() {
		super(new Item.Properties().tab(${data.creativeTab})<#if data.usageCount != 0>.durability(${data.usageCount})<#else>.stacksTo(${data.stackSize})</#if>);
		setRegistryName("${registryname}");
	}

	@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return new InteractionResultHolder(InteractionResult.SUCCESS, entity.getItemInHand(hand));
	}

	<@onEntitySwing data.onEntitySwing/>

	<#if data.specialInfo?has_content>
	@Override public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
		</#list>
	}
	</#if>

	@Override public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.${data.animation?upper_case};
	}

	@Override public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean isFoil(ItemStack itemstack) {
	    <#if hasProcedure(data.glowCondition)>
		Player entity = Minecraft.getInstance().player;
		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
    	if (!(<@procedureOBJToConditionCode data.glowCondition/>)) {
    	    return false;
    	}
    	</#if>
		return true;
	}
    </#if>

	<#if data.enableMeleeDamage>
		@Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
			if (slot == EquipmentSlot.MAINHAND) {
				ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
				builder.putAll(super.getDefaultAttributeModifiers(slot));
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Ranged item modifier", (double) ${data.damageVsEntity - 2}, AttributeModifier.Operation.ADDITION));
				builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Ranged item modifier", -2.4, AttributeModifier.Operation.ADDITION));
				return builder.build();
			}
			return super.getDefaultAttributeModifiers(slot);
		}
	</#if>

	<#if data.shootConstantly>
		@Override public void onUsingTick(ItemStack itemstack, LivingEntity entityLiving, int count) {
			Level world = entityLiving.level;
			if (!world.isClientSide() && entityLiving instanceof ServerPlayer entity) {
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				if (<@procedureOBJToConditionCode data.useCondition/>) {
					<@arrowShootCode/>
					entity.releaseUsingItem();
				}
			}
		}
    <#else>
		@Override
		public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entityLiving, int timeLeft) {
			if (!world.isClientSide() && entityLiving instanceof ServerPlayer entity) {
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				if (<@procedureOBJToConditionCode data.useCondition/>) {
					<@arrowShootCode/>
				}
			}
		}
    </#if>

}

<#macro arrowShootCode>
	<#if !data.ammoItem.isEmpty()>
	ItemStack stack = ProjectileWeaponItem.getHeldProjectile(entity, e -> e.getItem() == ${mappedMCItemToItem(data.ammoItem)});

	if(stack == ItemStack.EMPTY) {
		for (int i = 0; i < entity.getInventory().items.size(); i++) {
			ItemStack teststack = entity.getInventory().items.get(i);
			if(teststack != null && teststack.getItem() == ${mappedMCItemToItem(data.ammoItem)}) {
				stack = teststack;
				break;
			}
		}
	}

	if (entity.getAbilities().instabuild || stack != ItemStack.EMPTY) {
	</#if>

	${name}Entity entityarrow = ${name}Entity.shoot(world, entity, world.getRandom(), ${data.bulletPower}f, ${data.bulletDamage}, ${data.bulletKnockback});

	itemstack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));

	<#if !data.ammoItem.isEmpty()>
	if (entity.getAbilities().instabuild) {
		entityarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
	} else {
		if (${mappedMCItemToItemStackCode(data.ammoItem, 1)}.isDamageableItem()){
			if (stack.hurt(1, world.getRandom(), entity)) {
				stack.shrink(1);
				stack.setDamageValue(0);
            	if (stack.isEmpty())
               		entity.getInventory().removeItem(stack);
			}
		} else{
			stack.shrink(1);
            if (stack.isEmpty())
               entity.getInventory().removeItem(stack);
		}
	}
	<#else>
	entityarrow.pickup = AbstractArrow.Pickup.DISALLOWED;
	</#if>

	<#if hasProcedure(data.onRangedItemUsed)>
		<@procedureOBJToCode data.onRangedItemUsed/>
	</#if>

	<#if !data.ammoItem.isEmpty()>
	}
	</#if>
</#macro>

<#-- @formatter:on -->