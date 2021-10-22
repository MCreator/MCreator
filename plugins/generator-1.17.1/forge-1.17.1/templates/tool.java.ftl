<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.minecraft.world.entity.ai.attributes.Attributes;

<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe" || data.toolType == "Shears">
public class ${name}Item extends ${data.toolType.replace("Spade", "Shovel")}Item {
	public ${name}Item () {
		super(<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe">
			new Tier() {
				public int getUses() {
					return ${data.usageCount};
				}

   				public float getSpeed() {
					return ${data.efficiency}f;
				}

   				public float getAttackDamageBonus() {
					return ${data.damageVsEntity - 2}f;
				}

   				public int getLevel() {
					return ${data.harvestLevel};
				}

   				public int getEnchantmentValue() {
					return ${data.enchantability};
				}

   				public Ingredient getRepairIngredient() {
					<#if data.repairItems?has_content>
					return Ingredient.of(
							<#list data.repairItems as repairItem>
							${mappedMCItemToItemStackCode(repairItem,1)}<#if repairItem?has_next>,</#if>
                    </#list>
							);
					<#else>
					return Ingredient.EMPTY;
					</#if>
				}
			}, <#if data.toolType=="Sword">3<#elseif data.toolType=="Hoe">0<#else>1</#if>
			 ,${data.attackSpeed - 4}f, new Item.Properties()
			 	.tab(${data.creativeTab})
			 	<#if data.immuneToFire>
			 	.fireResistant()
			 	</#if>
		<#elseif data.toolType=="Shears">
			new Item.Properties()
				.tab(${data.creativeTab})
				.durability(${data.usageCount})
				<#if data.immuneToFire>
				.fireResistant()
				</#if>
		</#if>);

		setRegistryName("${registryname}");
	}

	<#if data.toolType=="Shears">
		@Override public int getEnchantmentValue() {
    	    return ${data.enchantability};
    	}

    	@Override public float getDestroySpeed(ItemStack stack, BlockState block) {
    		return ${data.efficiency}f;
    	}
    </#if>

    <#if hasProcedure(data.onBlockDestroyedWithTool)>
    	@Override public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity){
			boolean retval = super.mineBlock(itemstack,world,blockstate,pos,entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
            <@procedureOBJToCode data.onBlockDestroyedWithTool/>
			return retval;
		}
	</#if>

	<#if hasProcedure(data.onEntityHitWith)>
    	@Override public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
			boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			Level world = entity.level;
    		<@procedureOBJToCode data.onEntityHitWith/>
			return retval;
		}
	</#if>

    <#if hasProcedure(data.onRightClickedInAir)>
    	@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
			InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
			ItemStack itemstack = ar.getObject();
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
    		<@procedureOBJToCode data.onRightClickedInAir/>
			return ar;
		}
	</#if>
	
    <@commonMethods/>
}
<#elseif data.toolType=="Special">
public class ${name}Item extends Item {

    public ${name}Item() {
	    super(new Item.Properties()
		    .tab(${data.creativeTab})
			.durability(${data.usageCount})
			<#if data.immuneToFire>
			.fireResistant()
			</#if>
		);

		setRegistryName("${registryname}");
	}

	@Override public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
    	return List.of(
			<#list data.blocksAffected as restrictionBlock>
			${mappedBlockToBlock(restrictionBlock)}<#if restrictionBlock?has_next>,</#if>
			</#list>
		).contains(blockstate.getBlock()) ? ${data.efficiency}f : 1;
	}

	@Override public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		stack.hurtAndBreak(1, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		<#if hasProcedure(data.onBlockDestroyedWithTool)>
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onBlockDestroyedWithTool/>
		</#if>
		return true;
	}

	@Override public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity sourceentity) {
		stack.hurtAndBreak(2, sourceentity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		<#if hasProcedure(data.onEntityHitWith)>
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			Level world = entity.level;
			<@procedureOBJToCode data.onEntityHitWith/>
		</#if>
		return true;
	}
	
    <#if hasProcedure(data.onRightClickedInAir)>
    	@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
			InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
			ItemStack itemstack = ar.getObject();
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
    		<@procedureOBJToCode data.onRightClickedInAir/>
			return ar;
		}
	</#if>

	@Override public int getEnchantmentValue() {
		return ${data.enchantability};
	}

	@Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", ${data.damageVsEntity - 2}f, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", ${data.attackSpeed - 4}, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}

   	   return super.getDefaultAttributeModifiers(equipmentSlot);
   	}

    <@commonMethods/>
}
<#elseif data.toolType=="Fishing rod">
public class ${name}Item extends FishingRodItem {

	public ${name}Item() {
		super(new Item.Properties()
			.tab(${data.creativeTab})
			.durability(${data.usageCount})
			<#if data.immuneToFire>
			.fireResistant()
			</#if>
		);

		setRegistryName("${registryname}");
	}

	<#if data.repairItems?has_content>
	@Override public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return List.of(
			<#list data.repairItems as repairItem>
				${mappedMCItemToItem(repairItem)}<#if repairItem?has_next>,</#if>
			</#list>
		).contains(repairitem.getItem());
	}
	</#if>

	@Override public int getEnchantmentValue() {
		return ${data.enchantability};
	}

    <#if hasProcedure(data.onBlockDestroyedWithTool)>
    	@Override public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity){
			boolean retval = super.mineBlock(itemstack,world,blockstate,pos,entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
            <@procedureOBJToCode data.onBlockDestroyedWithTool/>
			return retval;
		}
	</#if>

	<#if hasProcedure(data.onEntityHitWith)>
    	@Override public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
			boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			Level world = entity.level;
    		<@procedureOBJToCode data.onEntityHitWith/>
			return retval;
		}
	</#if>
	
    
	@Override public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		ItemStack itemstack = entity.getItemInHand(hand);
		if (entity.fishing != null) {
			if (!world.isClientSide()) {
				itemstack.hurtAndBreak(entity.fishing.retrieve(itemstack), entity, i -> i.broadcastBreakEvent(hand));
			}
			world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
			world.gameEvent(entity, GameEvent.FISHING_ROD_REEL_IN, entity);
		} else {
			world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
			if (!world.isClientSide()) {
				int k = EnchantmentHelper.getFishingSpeedBonus(itemstack);
				int j = EnchantmentHelper.getFishingLuckBonus(itemstack);
				world.addFreshEntity(new FishingHook(entity, world, j, k) {
					 // TODO: custom action
				});
			}

			entity.awardStat(Stats.ITEM_USED.get(this));
			world.gameEvent(entity, GameEvent.FISHING_ROD_CAST, entity);
		}
		
		<#if hasProcedure(data.onRightClickedInAir)>
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onRightClickedInAir/>
		</#if>

		return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
	}
	

    <@commonMethods/>
}
</#if>

<#macro commonMethods>
	<#if data.stayInGridWhenCrafting>
        @Override public boolean hasContainerItem(ItemStack stack) {
        	return true;
        }

        <#if data.damageOnCrafting && data.usageCount != 0>
        	@Override public ItemStack getContainerItem(ItemStack itemstack) {
        		ItemStack retval = new ItemStack(this);
        		retval.setDamageValue(itemstack.getDamageValue() + 1);
        		if(retval.getDamageValue() >= retval.getMaxDamage()) {
        			return ItemStack.EMPTY;
        		}
        		return retval;
        	}

        	@Override public boolean isRepairable(ItemStack itemstack) {
        		return false;
        	}
        <#else>
        	@Override public ItemStack getContainerItem(ItemStack itemstack) {
        		return new ItemStack(this);
        	}

        	<#if data.usageCount != 0>
        	    @Override public boolean isRepairable(ItemStack itemstack) {
        		    return false;
        	    }
        	</#if>
        </#if>
    </#if>

    <#if data.specialInfo?has_content>
    	@Override public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
    		super.appendHoverText(itemstack, world, list, flag);
    		<#list data.specialInfo as entry>
    		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
    		</#list>
    	}
    </#if>

    <#if hasProcedure(data.onRightClickedOnBlock)>
    	@Override public InteractionResult useOn(UseOnContext context) {
    		InteractionResult retval = super.useOn(context);
    		Level world = context.getLevel();
    		BlockPos pos = context.getClickedPos();
    		Player entity = context.getPlayer();
    		Direction direction = context.getClickedFace();
    		BlockState blockstate = world.getBlockState(pos);
    		int x = pos.getX();
    		int y = pos.getY();
    		int z = pos.getZ();
    		ItemStack itemstack = context.getItemInHand();
    		<#if hasReturnValue(data.onRightClickedOnBlock)>
    		return <@procedureOBJToInteractionResultCode data.onRightClickedOnBlock/>;
    		<#else>
    		<@procedureOBJToCode data.onRightClickedOnBlock/>
    		return retval;
    		</#if>
    	}
    </#if>

    <#if hasProcedure(data.onCrafted)>
    	@Override public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
    		super.onCraftedBy(itemstack, world, entity);
    		double x = entity.getX();
    		double y = entity.getY();
    		double z = entity.getZ();
    		<@procedureOBJToCode data.onCrafted/>
    	}
    </#if>

    <#if hasProcedure(data.onEntitySwing)>
    	@Override public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
    		boolean retval = super.onEntitySwing(itemstack, entity);
    		double x = entity.getX();
    		double y = entity.getY();
    		double z = entity.getZ();
    		Level world = entity.level;
    		<@procedureOBJToCode data.onEntitySwing/>
    		return retval;
    	}
    </#if>

    <#if hasProcedure(data.onStoppedUsing)>
    	@Override public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
    		super.releaseUsing(itemstack, world, entity, time);
    		double x = entity.getX();
    		double y = entity.getY();
    		double z = entity.getZ();
    		<@procedureOBJToCode data.onStoppedUsing/>
    	}
    </#if>

    <#if hasProcedure(data.onItemInUseTick) || hasProcedure(data.onItemInInventoryTick)>
    	@Override public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
    		super.inventoryTick(itemstack, world, entity, slot, selected);
    		double x = entity.getX();
    		double y = entity.getY();
    		double z = entity.getZ();
    		<#if hasProcedure(data.onItemInUseTick)>
    		if (selected)
    			<@procedureOBJToCode data.onItemInUseTick/>
    		</#if>
    		<@procedureOBJToCode data.onItemInInventoryTick/>
    	}
    </#if>

    <#if data.hasGlow>
    	@Override public boolean isFoil(ItemStack itemstack) {
    		<#if hasProcedure(data.glowCondition)>
    		Player entity = Minecraft.getInstance().player;
    		Level world = entity.level;
    		double x = entity.getX();
    		double y = entity.getY();
    		double z = entity.getZ();
    		return <@procedureOBJToConditionCode data.glowCondition/>;
    		<#else>
    		return true;
    		</#if>
    	}
    </#if>
</#macro>
<#-- @formatter:on -->