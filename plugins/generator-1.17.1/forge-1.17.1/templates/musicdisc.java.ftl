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
<#include "triggers.java.ftl">

package ${package}.item;

import net.minecraft.network.chat.Component;

public class ${name}Item extends RecordItem {

	public ${name}Item() {
		<#if data.music.getUnmappedValue().startsWith("CUSTOM:")>
		super(0, ${JavaModName}Sounds.REGISTRY.get(new ResourceLocation("${data.music}")),
				new Item.Properties().tab(${data.creativeTab}).stacksTo(1).rarity(Rarity.RARE));
		<#else>
		super(0, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.music}")),
				new Item.Properties().tab(${data.creativeTab}).stacksTo(1).rarity(Rarity.RARE));
		</#if>

		setRegistryName("${registryname}");
	}

	<#if data.hasGlow>
	@Override @OnlyIn(Dist.CLIENT) public boolean isFoil(ItemStack itemstack) {
		return true;
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

	<@onRightClickedInAir data.onRightClickedInAir/>

	<@onItemUsedOnBlock data.onRightClickedOnBlock/>

	<@onEntityHitWith data.onEntityHitWith/>

	<@onEntitySwing data.onEntitySwing/>

	<@onCrafted data.onCrafted/>

	<@onStoppedUsing data.onStoppedUsing/>

	<@onItemTick data.onItemInUseTick, data.onItemInInventoryTick/>
}
<#-- @formatter:on -->