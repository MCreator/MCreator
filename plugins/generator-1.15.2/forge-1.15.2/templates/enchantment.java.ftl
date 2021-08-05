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

package ${package}.enchantment;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Enchantment extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final Enchantment enchantment = null;

	public ${name}Enchantment(${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.enchantments.add(() -> new CustomEnchantment(EquipmentSlotType.MAINHAND).setRegistryName("${registryname}"));
	}

	public static class CustomEnchantment extends Enchantment {

		public CustomEnchantment(EquipmentSlotType... slots) {
			super(Enchantment.Rarity.${data.rarity}, EnchantmentType.${generator.map(data.type, "enchantmenttypes")}, slots);
        }

		@Override public int getMinLevel() {
			return ${data.minLevel};
		}

		@Override public int getMaxLevel() {
			return ${data.maxLevel};
		}

		<#if data.damageModifier != 0>
		@Override public int calcModifierDamage(int level, DamageSource source) {
			return level * ${data.damageModifier};
		}
		</#if>

        <#if data.compatibleEnchantments?has_content>
		@Override protected boolean canApplyTogether(Enchantment ench) {
			<#list data.compatibleEnchantments as compatibleEnchantment>
			    if(ench == ${compatibleEnchantment})
			    	return true;
            </#list>
			return false;
		}
        </#if>

        <#if data.compatibleItems?has_content>
		@Override public boolean canApplyAtEnchantingTable(ItemStack stack) {
            <#list data.compatibleItems as compatibleItem>
			    if(stack.getItem() == ${mappedMCItemToItem(compatibleItem)})
					return true;
            </#list>
			return false;
		}
        </#if>

		@Override public boolean isTreasureEnchantment() {
			return ${data.isTreasureEnchantment};
		}

		@Override public boolean isCurse() {
			return ${data.isCurse};
		}

		@Override public boolean isAllowedOnBooks() {
			return ${data.isAllowedOnBooks};
		}

	}

}
<#-- @formatter:on -->