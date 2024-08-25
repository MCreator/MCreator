<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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

<#macro slotsCode slots>
	<#if slots == "any">EquipmentSlot.values()
	<#elseif slots == "hand">new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }
	<#elseif slots == "armor">new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }
	<#else>new EquipmentSlot[] { EquipmentSlot.${slots?upper_case} }
	</#if>
</#macro>

<#macro supportedItemsCode supportedItems slots>
	<#if supportedItems?size == 1 && supportedItems?first?starts_with("TAG:")>
		ItemTags.create(new ResourceLocation("${supportedItems?first?replace("TAG:", "")}"))
	<#else>
	<#-- we will override this in canApplyAtEnchantingTable anyway, but still try to match appropriate one here -->
		<#if slots == "armor" || slots == "feet" || slots == "legs" || slots == "chest" || slots == "head" || slots == "body">
		ItemTags.ARMOR_ENCHANTABLE
		<#else>
		ItemTags.MINING_ENCHANTABLE
		</#if>
	</#if>
</#macro>

package ${package}.enchantment;

public class ${name}Enchantment extends Enchantment {

	public ${name}Enchantment() {
		super(Enchantment.definition(
			<@supportedItemsCode supportedItems data.supportedSlots/>, <#-- supportedItems -->
			${data.weight}, <#-- weight -->
			${data.maxLevel}, <#-- maxLevel -->
			Enchantment.dynamicCost(1, 10), <#-- minCost -->
			Enchantment.dynamicCost(6, 10), <#-- maxCost -->
			${data.anvilCost}, <#-- anvilCost -->
			<@slotsCode data.supportedSlots/> <#-- slots -->
		));
	}

	@Override public boolean canApplyAtEnchantingTable(ItemStack itemstack) {
		return ${mappedMCItemsToIngredient(supportedItems)}.test(itemstack);
	}

	<#if data.damageModifier != 0>
	@Override public int getDamageProtection(int level, DamageSource source) {
		return level * ${data.damageModifier};
	}
	</#if>

	<#if incompatibleEnchantments?has_content && !incompatibleEnchantments?first?starts_with("#")>
	@Override protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !List.of(
			<#list incompatibleEnchantments as incompatibleEnchantment>
				${incompatibleEnchantment}<#sep>,
			</#list>
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