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

<#assign supportedItems = w.filterBrokenReferences(data.supportedItems)>
<#assign incompatibleEnchantments = w.filterBrokenReferences(data.incompatibleEnchantments)>

<#macro weightToRarity weight>
	<#if weight <= 1>VERY_RARE
	<#elseif weight <= 2>RARE
	<#elseif weight <= 5>UNCOMMON
	<#else>COMMON
	</#if>
</#macro>

<#macro slotsCode slots>
	<#if slots == "any">EquipmentSlot.values()
	<#elseif slots == "hand">new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }
	<#elseif slots == "armor">new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }
	<#elseif slots == "body">new EquipmentSlot[] { EquipmentSlot.CHEST }
	<#else>new EquipmentSlot[] { EquipmentSlot.${slots?upper_case} }
	</#if>
</#macro>

package ${package}.enchantment;

public class ${name}Enchantment extends Enchantment {

	private static final EnchantmentCategory ENCHANTMENT_CATEGORY =
		<#if supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/foot_armor">EnchantmentCategory.ARMOR_FEET;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/leg_armor">EnchantmentCategory.ARMOR_LEGS;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/chest_armor">EnchantmentCategory.ARMOR_CHEST;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/head_armor">EnchantmentCategory.ARMOR_HEAD;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/armor">EnchantmentCategory.ARMOR;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/weapon">EnchantmentCategory.WEAPON;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/mining">EnchantmentCategory.DIGGER;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/fishing">EnchantmentCategory.FISHING_ROD;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/trident">EnchantmentCategory.TRIDENT;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/durability">EnchantmentCategory.BREAKABLE;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/bow">EnchantmentCategory.BOW;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/equippable">EnchantmentCategory.WEARABLE;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/crossbow">EnchantmentCategory.CROSSBOW;
		<#elseif supportedItems?size == 1 && supportedItems?first == "TAG:minecraft:enchantable/vanishing">EnchantmentCategory.VANISHABLE;
		<#else>EnchantmentCategory.create("${modid}_${registryname}", item -> ${mappedMCItemsToIngredient(supportedItems)}.test(new ItemStack(item)));
		</#if>

	public ${name}Enchantment() {
		super(Enchantment.Rarity.<@weightToRarity data.weight/>, ENCHANTMENT_CATEGORY, <@slotsCode data.supportedSlots/>);
	}

	@Override public int getMinCost(int level) {
		return 1 + level * 10;
	}

	@Override public int getMaxCost(int level) {
		return 6 + level * 10;
	}

	<#if data.maxLevel != 1>
	@Override public int getMaxLevel() {
		return ${data.maxLevel};
	}
	</#if>

	<#if data.damageModifier != 0>
	@Override public int getDamageProtection(int level, DamageSource source) {
		return level * ${data.damageModifier};
	}
	</#if>

	<#if incompatibleEnchantments?has_content>
	@Override protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !List.of(
			<#list incompatibleEnchantments as incompatibleEnchantment>${incompatibleEnchantment}<#sep>,</#list>
		).contains(enchantment);
	}
	</#if>

	<#if data.isTreasureEnchantment>
	@Override public boolean isTreasureOnly() {
		return true;
	}
	</#if>

	<#if data.isCurse>
	@Override public boolean isCurse() {
		return true;
	}
	</#if>

	<#if !data.canGenerateInLootTables>
	@Override public boolean isDiscoverable() {
		return false;
	}
	</#if>

	<#if !data.canVillagerTrade>
	@Override public boolean isTradeable() {
		return false;
	}
	</#if>

}
<#-- @formatter:on -->