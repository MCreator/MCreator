<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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
<#include "triggers.java.ftl">

package ${package}.item;

<#compress>
<#if (data.usageCount == 0) && (data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe" || data.toolType == "MultiTool")>
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
</#if>
<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade"
		|| data.toolType == "Hoe" || data.toolType == "Shears" || data.toolType == "Shield" || data.toolType == "MultiTool">
public class ${name}Item extends ${data.toolType?replace("Spade", "Shovel")?replace("MultiTool", "")}Item {

	<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe" || data.toolType == "MultiTool">
	private static final ToolMaterial TOOL_MATERIAL = new ToolMaterial(
		<#if data.blockDropsTier == "WOOD">BlockTags.INCORRECT_FOR_WOODEN_TOOL
		<#elseif data.blockDropsTier == "STONE">BlockTags.INCORRECT_FOR_STONE_TOOL
		<#elseif data.blockDropsTier == "IRON">BlockTags.INCORRECT_FOR_IRON_TOOL
		<#elseif data.blockDropsTier == "DIAMOND">BlockTags.INCORRECT_FOR_DIAMOND_TOOL
		<#elseif data.blockDropsTier == "GOLD">BlockTags.INCORRECT_FOR_GOLD_TOOL
		<#else>BlockTags.INCORRECT_FOR_NETHERITE_TOOL
		</#if>,
		${data.usageCount},
		${data.efficiency}f,
		0,
		${data.enchantability},
		TagKey.create(Registries.ITEM, ResourceLocation.parse("${modid}:${registryname}_repair_items")) <#-- data.repairItems are put into a tag -->
	);
	</#if>

	public ${name}Item (Item.Properties properties) {
		super(
			<#if data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe">
			TOOL_MATERIAL, ${data.damageVsEntity - 1}f, ${data.attackSpeed - 4}f,
			</#if>
			<#if data.toolType == "MultiTool">
			TOOL_MATERIAL.applyToolProperties(properties, BlockTags.MINEABLE_WITH_PICKAXE, ${data.damageVsEntity - 1}f, ${data.attackSpeed - 4}f)
			<#else>
			properties
			</#if>
				<#if (data.usageCount != 0) && (data.toolType == "Shears" || data.toolType == "Shield")>
				.durability(${data.usageCount})
				</#if>
				<#if data.toolType == "MultiTool">
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ${data.damageVsEntity - 1},
								AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
						.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, ${data.attackSpeed - 4},
								AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
						.build())
				</#if>
				<#if data.immuneToFire>
				.fireResistant()
				</#if>
				<#if data.toolType == "Shield">
				.repairable(TagKey.create(Registries.ITEM, ResourceLocation.parse("${modid}:${registryname}_repair_items")))
				</#if>
				<#if data.enchantability != 0 && data.toolType=="Shears">
				.enchantable(${data.enchantability})
				</#if>
		);
	}

	<#if (data.usageCount == 0) && (data.toolType == "Pickaxe" || data.toolType == "Axe" || data.toolType == "Sword" || data.toolType == "Spade" || data.toolType == "Hoe" || data.toolType == "MultiTool")>
	@SubscribeEvent public static void handleToolDamage(ModifyDefaultComponentsEvent event) {
		event.modify(${JavaModName}Items.${REGISTRYNAME}.get(), builder -> builder.remove(DataComponents.MAX_DAMAGE));
	}
	</#if>

	<#if hasProcedure(data.additionalDropCondition) && data.toolType!="MultiTool">
	@Override public boolean isCorrectToolForDrops(ItemStack itemstack, BlockState blockstate) {
		return super.isCorrectToolForDrops(itemstack, blockstate) && <@procedureCode data.additionalDropCondition, {
		"itemstack": "itemstack",
		"blockstate": "blockstate"
		}, false/>;
	}
	</#if>

	<#if data.toolType=="Shears">
		@Override public float getDestroySpeed(ItemStack stack, BlockState blockstate) {
			return ${data.efficiency}f;
		}
	<#elseif data.toolType=="MultiTool">
		@Override public boolean isCorrectToolForDrops(ItemStack itemstack, BlockState blockstate) {
			<#if hasProcedure(data.additionalDropCondition)>
				if(!<@procedureCode data.additionalDropCondition, {
					"itemstack": "itemstack",
					"blockstate": "blockstate"
				}, false/>) return false;
			</#if>

			<#if data.blockDropsTier == "WOOD" || data.blockDropsTier == "GOLD">
			return !blockstate.is(BlockTags.NEEDS_STONE_TOOL) && !blockstate.is(BlockTags.NEEDS_IRON_TOOL) && !blockstate.is(BlockTags.NEEDS_DIAMOND_TOOL);
			<#elseif data.blockDropsTier == "STONE">
			return !blockstate.is(BlockTags.NEEDS_IRON_TOOL) && !blockstate.is(BlockTags.NEEDS_DIAMOND_TOOL);
			<#elseif data.blockDropsTier == "IRON">
			return !blockstate.is(BlockTags.NEEDS_DIAMOND_TOOL);
			<#else>
			return blockstate.is(BlockTags.MINEABLE_WITH_AXE) || blockstate.is(BlockTags.MINEABLE_WITH_HOE) || blockstate.is(BlockTags.MINEABLE_WITH_PICKAXE) || blockstate.is(BlockTags.MINEABLE_WITH_SHOVEL);
			</#if>
		}

		@Override public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
			return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
					ItemAbilities.DEFAULT_HOE_ACTIONS.contains(toolAction) ||
					ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(toolAction) ||
					ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
					ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(toolAction);
		}

		@Override public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
			return ${data.efficiency}f;
		}
	</#if>

	<#if data.toolType=="MultiTool">
		<@onBlockDestroyedWith data.onBlockDestroyedWithTool, true/>

		<@onEntityHitWith data.onEntityHitWith, true/>
	<#else>
		<@onBlockDestroyedWith data.onBlockDestroyedWithTool/>

		<@onEntityHitWith data.onEntityHitWith/>
	</#if>

	<@onRightClickedInAir data.onRightClickedInAir/>

	<@commonMethods/>

}
<#elseif data.toolType=="Special">
public class ${name}Item extends Item {

	public ${name}Item(Item.Properties properties) {
		super(properties
			<#if data.usageCount != 0>
			.durability(${data.usageCount})
			</#if>
			<#if data.immuneToFire>
			.fireResistant()
			</#if>
			.attributes(ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ${data.damageVsEntity - 1},
						AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, ${data.attackSpeed - 4},
						AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build())
			<#if data.enchantability != 0>
			.enchantable(${data.enchantability})
			</#if>
		);
	}

	@Override public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
		return <#if data.blocksAffected?has_content>${containsAnyOfBlocks(data.blocksAffected "blockstate")} ? ${data.efficiency}f : </#if>1;
	}

	<@onBlockDestroyedWith data.onBlockDestroyedWithTool, true/>

	<@onEntityHitWith data.onEntityHitWith, true/>

	<@onRightClickedInAir data.onRightClickedInAir/>

	<@commonMethods/>
}
<#elseif data.toolType=="Fishing rod">
public class ${name}Item extends FishingRodItem {

	public ${name}Item(Item.Properties properties) {
		super(properties
			<#if data.usageCount != 0>
			.durability(${data.usageCount})
			</#if>
			<#if data.immuneToFire>
			.fireResistant()
			</#if>
			.repairable(TagKey.create(Registries.ITEM, ResourceLocation.parse("${modid}:${registryname}_repair_items")))
			<#if data.enchantability != 0>
			.enchantable(${data.enchantability})
			</#if>
		);
	}

	<@onBlockDestroyedWith data.onBlockDestroyedWithTool/>

	<@onEntityHitWith data.onEntityHitWith/>

	<#if hasProcedure(data.onRightClickedInAir)>
	@Override public InteractionResult use(Level world, Player entity, InteractionHand hand) {
		super.use(world, entity, hand);
		ItemStack itemstack = entity.getItemInHand(hand);
		<@procedureCode data.onRightClickedInAir, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "world",
			"entity": "entity",
			"itemstack": "itemstack"
		}/>

		return InteractionResult.SUCCESS;
	}
	</#if>

	<@commonMethods/>
}
</#if>
</#compress>

<#macro commonMethods>
	<#if data.stayInGridWhenCrafting>
		<#if data.damageOnCrafting && data.usageCount != 0>
			@Override public ItemStack getCraftingRemainder(ItemStack itemstack) {
				ItemStack retval = new ItemStack(this);
				retval.setDamageValue(itemstack.getDamageValue() + 1);
				if(retval.getDamageValue() >= retval.getMaxDamage()) {
					return ItemStack.EMPTY;
				}
				return retval;
			}

			@Override public boolean isCombineRepairable(ItemStack itemstack) {
				return false;
			}
		<#else>
			@Override public ItemStack getCraftingRemainder(ItemStack itemstack) {
				return new ItemStack(this);
			}

			<#if data.usageCount != 0>
			@Override public boolean isCombineRepairable(ItemStack itemstack) {
				return false;
			}
			</#if>
		</#if>
	</#if>

	<@addSpecialInformation data.specialInformation, "item." + modid + "." + registryname/>

	<@onItemUsedOnBlock data.onRightClickedOnBlock/>

	<@onCrafted data.onCrafted/>

	<@onEntitySwing data.onEntitySwing/>

	<@onItemTick data.onItemInUseTick, data.onItemInInventoryTick/>

	<@hasGlow data.glowCondition/>

</#macro>
<#-- @formatter:on -->