<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasBlocks = false>
<#assign hasDoubleBlocks = false>

public class ${JavaModName}Items {

	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ${JavaModName}.MODID);

	<#list items as item>
		<#if item.getModElement().getTypeString() == "fluid" && item.generateBucket>
			public static final RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_BUCKET =
				REGISTRY.register("${item.getModElement().getRegistryName()}_bucket", () -> new ${item.getModElement().getName()}Item());
		<#elseif item.getModElement().getType().getBaseType()?string == "BLOCK">
			<#if (item.getModElement().getTypeString() == "block" && item.isDoubleBlock()) || (item.getModElement().getTypeString() == "plant" && item.isDoubleBlock())>
				<#assign hasDoubleBlocks = true>
				public static final RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()} =
					doubleBlock(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()}, ${item.creativeTab});
			<#else>
				<#assign hasBlocks = true>
				public static final RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()} =
					block(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()}, ${item.creativeTab});
			</#if>
		<#else>
			public static final RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()} =
				REGISTRY.register("${item.getModElement().getRegistryName()}", () -> new ${item.getModElement().getName()}Item());
		</#if>
	</#list>

	<#if hasBlocks>
	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
	</#if>

	<#if hasDoubleBlocks>
	private static RegistryObject<Item> doubleBlock(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties().tab(tab)));
	}
	</#if>
}

<#-- @formatter:on -->