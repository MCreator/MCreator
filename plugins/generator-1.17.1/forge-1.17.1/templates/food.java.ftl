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
<#include "procedures.java.ftl">
<#include "triggers.java.ftl">
<#include "mcitems.ftl">

package ${package}.item;


public class ${name}Item extends Item {

	public ${name}Item() {
		super(new Item.Properties().tab(${data.creativeTab}).stacksTo(${data.stackSize}).rarity(Rarity.${data.rarity})
			.food((new FoodProperties.Builder()).nutrition(${data.nutritionalValue}).saturationMod(${data.saturation}f)
			<#if data.isAlwaysEdible>.alwaysEat()</#if>
			<#if data.forDogs>.meat()</#if>
			.build()));
		setRegistryName("${registryname}");
	}

	<#if data.eatingSpeed != 32>
	@Override public int getUseDuration(ItemStack stack) {
		return ${data.eatingSpeed};
	}
	</#if>

	<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean isFoil(ItemStack itemstack) {
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

	<#if data.animation != "eat">
	@Override public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.${data.animation?upper_case};
	}
	</#if>

	<#if data.specialInfo?has_content>
	@Override public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		<#list data.specialInfo as entry>
		list.add(new TextComponent("${JavaConventions.escapeStringForJava(entry)}"));
		</#list>
	}
	</#if>

	<@onRightClickedInAir data.onRightClicked/>

	<@onItemUseFirst data.onRightClickedOnBlock/>

	<#if hasProcedure(data.onEaten) || (data.resultItem?? && !data.resultItem.isEmpty())>
	@Override public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval =
			<#if data.resultItem?? && !data.resultItem.isEmpty()>
				${mappedMCItemToItemStackCode(data.resultItem, 1)};
			</#if>
		super.finishUsingItem(itemstack, world, entity);

		<#if hasProcedure(data.onEaten)>
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			<@procedureOBJToCode data.onEaten/>
		</#if>

		<#if data.resultItem?? && !data.resultItem.isEmpty()>
			if (itemstack.isEmpty()) {
				return retval;
			} else {
				if (entity instanceof Player player && !player.getAbilities().instabuild) {
					if (!player.getInventory().add(retval))
						player.drop(retval, false);
				}
				return itemstack;
			}
		<#else>
			return retval;
		</#if>
	}
	</#if>

	<@onEntityHitWith data.onEntityHitWith/>

	<@onEntitySwing data.onEntitySwing/>

	<@onCrafted data.onCrafted/>

	<@onItemTick data.onItemInUseTick, data.onItemInInventoryTick/>

	<@onDroppedByPlayer data.onDroppedByPlayer/>

}
<#-- @formatter:on -->