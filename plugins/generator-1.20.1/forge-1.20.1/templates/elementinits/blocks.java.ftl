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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasTintedBlocks = false>
<#assign hasTintedBlockItems = false>
<#list blocks as block>
	<#if block.getModElement().getTypeString() == "block">
		<#if block.tintType != "No tint">
			<#assign hasTintedBlocks = true>
			<#if block.isItemTinted>
				<#assign hasTintedBlockItems = true>
			</#if>
		</#if>
	<#elseif block.getModElement().getTypeString() == "plant">
		<#if block.tintType != "No tint">
			<#assign hasTintedBlocks = true>
			<#if block.isItemTinted>
				<#assign hasTintedBlockItems = true>
			</#if>
		</#if>
	</#if>
</#list>

public class ${JavaModName}Blocks {

	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ${JavaModName}.MODID);

	<#list blocks as block>
		<#if block.getModElement().getTypeString() == "dimension">
            public static RegistryObject<Block> ${block.getModElement().getRegistryNameUpper()}_PORTAL;
		<#else>
			public static RegistryObject<Block> ${block.getModElement().getRegistryNameUpper()};
		</#if>
	</#list>

	<#assign chunks = blocks?chunk(2500)>
	<#assign chunks_num = chunks?size>
	<#list chunks as sub_blocks>
	public static void register<#if chunks_num == 1>(IEventBus modEventBus)<#else>${sub_blocks?index}()</#if> {
		<#list sub_blocks as block>
			<#if block.getModElement().getTypeString() == "dimension">
        	    ${block.getModElement().getRegistryNameUpper()}_PORTAL =
					REGISTRY.register("${block.getModElement().getRegistryName()}_portal", ${block.getModElement().getName()}PortalBlock::new);
			<#else>
				${block.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${block.getModElement().getRegistryName()}", ${block.getModElement().getName()}Block::new);
			</#if>
		</#list>
		<#if chunks_num == 1>REGISTRY.register(modEventBus);</#if>
	}
	</#list>

	<#if chunks_num gt 1>
	public static void register(IEventBus modEventBus) {
		<#list 0..chunks_num-1 as i>register${i}();</#list>
		REGISTRY.register(modEventBus);
	}
	</#if>

	// Start of user code block custom blocks
	// End of user code block custom blocks

	<#if hasTintedBlocks || hasTintedBlockItems>
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public static class BlocksClientSideHandler {
		<#if hasTintedBlocks>
		@SubscribeEvent public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
			<#list blocks as block>
				<#if block.getModElement().getTypeString() == "block" || block.getModElement().getTypeString() == "plant">
					<#if block.tintType != "No tint">
						 ${block.getModElement().getName()}Block.blockColorLoad(event);
					</#if>
				</#if>
			</#list>
		}
		</#if>

		<#if hasTintedBlockItems>
		@SubscribeEvent public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
			<#list blocks as block>
				<#if block.getModElement().getTypeString() == "block" || block.getModElement().getTypeString() == "plant">
					<#if block.tintType != "No tint" && block.isItemTinted>
						 ${block.getModElement().getName()}Block.itemColorLoad(event);
					</#if>
				</#if>
			</#list>
		}
		</#if>
	}
	</#if>

}

<#-- @formatter:on -->