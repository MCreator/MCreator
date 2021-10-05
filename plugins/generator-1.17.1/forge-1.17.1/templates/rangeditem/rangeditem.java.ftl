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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.item;

public class ${name}Item extends Item {

	public ${name}Item() {
		super(new Item.Properties().group(${data.creativeTab})<#if data.usageCount != 0>.maxDamage(${data.usageCount})<#else>.maxStackSize(${data.stackSize})</#if>);
		setRegistryName("${registryname}");
	}

	@Override public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entity, Hand hand) {
		entity.setActiveHand(hand);
		return new ActionResult(ActionResultType.SUCCESS, entity.getHeldItem(hand));
	}

	<#if hasProcedure(data.onEntitySwing)>
	@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();
		World world = entity.world;
		<@procedureOBJToCode data.onEntitySwing/>
		return retval;
	}
	</#if>

	<#if data.specialInfo?has_content>
	@Override public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
		</#list>
	}
	</#if>

	@Override public UseAction getUseAction(ItemStack itemstack) {
		return UseAction.${data.animation?upper_case};
	}

	@Override public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean hasEffect(ItemStack itemstack) {
	    <#if hasProcedure(data.glowCondition)>
		PlayerEntity entity = Minecraft.getInstance().player;
		World world = entity.world;
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();
    	if (!(<@procedureOBJToConditionCode data.glowCondition/>)) {
    	    return false;
    	}
    	</#if>
		return true;
	}
    </#if>

	<#if data.enableMeleeDamage>
		@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
			if (slot == EquipmentSlotType.MAINHAND) {
				ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
				builder.putAll(super.getAttributeModifiers(slot));
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) ${data.damageVsEntity - 2}, AttributeModifier.Operation.ADDITION));
				builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, AttributeModifier.Operation.ADDITION));
				return builder.build();
			}
			return super.getAttributeModifiers(slot);
		}
	</#if>

	<#if data.shootConstantly>
		@Override public void onUsingTick(ItemStack itemstack, LivingEntity entityLiving, int count) {
			World world = entityLiving.world;
			if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
				ServerPlayerEntity entity = (ServerPlayerEntity) entityLiving;
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				if (<@procedureOBJToConditionCode data.useCondition/>) {
					<@arrowShootCode/>
					entity.stopActiveHand();
				}
			}
		}
    <#else>
		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, LivingEntity entityLiving, int timeLeft) {
			if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
				ServerPlayerEntity entity = (ServerPlayerEntity) entityLiving;
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				if (<@procedureOBJToConditionCode data.useCondition/>) {
					<@arrowShootCode/>
				}
			}
		}
    </#if>

}

<#macro arrowShootCode>
	<#if !data.ammoItem.isEmpty()>
	ItemStack stack = ShootableItem.getHeldAmmo(entity, e -> e.getItem() == ${mappedMCItemToItem(data.ammoItem)});

	if(stack == ItemStack.EMPTY) {
		for (int i = 0; i < entity.inventory.mainInventory.size(); i++) {
			ItemStack teststack = entity.inventory.mainInventory.get(i);
			if(teststack != null && teststack.getItem() == ${mappedMCItemToItem(data.ammoItem)}) {
				stack = teststack;
				break;
			}
		}
	}

	if (entity.abilities.isCreativeMode || stack != ItemStack.EMPTY) {
	</#if>

	ArrowCustomEntity entityarrow = shoot(world, entity, random, ${data.bulletPower}f, ${data.bulletDamage}, ${data.bulletKnockback});

	itemstack.damageItem(1, entity, e -> e.sendBreakAnimation(entity.getActiveHand()));

	<#if !data.ammoItem.isEmpty()>
	if (entity.abilities.isCreativeMode) {
		entityarrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
	} else {
		if (${mappedMCItemToItemStackCode(data.ammoItem, 1)}.isDamageable()){
			if (stack.attemptDamageItem(1, random, entity)) {
				stack.shrink(1);
				stack.setDamage(0);
            	if (stack.isEmpty())
               		entity.inventory.deleteStack(stack);
			}
		} else{
			stack.shrink(1);
            if (stack.isEmpty())
               entity.inventory.deleteStack(stack);
		}
	}
	<#else>
	entityarrow.pickupStatus = AbstractArrowEntity.PickupStatus.DISALLOWED;
	</#if>

	<#if hasProcedure(data.onRangedItemUsed)>
		<@procedureOBJToCode data.onRangedItemUsed/>
	</#if>

	<#if !data.ammoItem.isEmpty()>
	}
	</#if>
</#macro>

<#-- @formatter:on -->