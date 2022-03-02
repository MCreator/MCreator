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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasTransparentBlocks = false>
<#assign hasTintedBlocks = false>
<#assign hasTintedBlockItems = false>
<#list blocks as block>
    <#if block.getModElement().getTypeString() == "block">
        <#if block.transparencyType != "SOLID" || block.hasTransparency || block.tintType != "No tint">
            <#assign hasTransparentBlocks = true>
        </#if>
        <#if block.tintType != "No tint">
            <#assign hasTintedBlocks = true>
            <#if block.isItemTinted>
                <#assign hasTintedBlockItems = true>
            </#if>
        </#if>
    <#elseif block.getModElement().getTypeString() == "plant">
        <#assign hasTransparentBlocks = true> <#-- Plants always have cutout transparency -->
        <#if block.tintType != "No tint">
            <#assign hasTintedBlocks = true>
            <#if block.isItemTinted>
                <#assign hasTintedBlockItems = true>
            </#if>
        </#if>
    </#if>
</#list>

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Blocks {

    private static final List<Block> REGISTRY = new ArrayList<>();

    <#list blocks as block>
        <#if block.getModElement().getTypeString() == "dimension">
            public static final Block ${block.getModElement().getRegistryNameUpper()}_PORTAL = register(new ${block.getModElement().getName()}PortalBlock());
        <#else>
            public static final Block ${block.getModElement().getRegistryNameUpper()} = register(new ${block.getModElement().getName()}Block());
        </#if>
    </#list>

    private static Block register(Block block) {
		REGISTRY.add(block);
    	return block;
    }

	@SubscribeEvent public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new Block[0]));
	}

	<#if hasTransparentBlocks || hasTintedBlocks || hasTintedBlockItems>
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public static class ClientSideHandler {

        <#if hasTransparentBlocks>
	    @SubscribeEvent public static void clientSetup(FMLClientSetupEvent event) {
	    	<#list blocks as block>
                <#if block.getModElement().getTypeString() == "block">
                    <#if block.transparencyType != "SOLID" || block.hasTransparency>
                        ${block.getModElement().getName()}Block.registerRenderLayer();
                    </#if>
                <#elseif block.getModElement().getTypeString() == "plant">
                    ${block.getModElement().getName()}Block.registerRenderLayer();
                </#if>
            </#list>
		}
        </#if>

        <#if hasTintedBlocks>
        @SubscribeEvent public static void blockColorLoad(ColorHandlerEvent.Block event) {
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
        @SubscribeEvent public static void itemColorLoad(ColorHandlerEvent.Item event) {
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