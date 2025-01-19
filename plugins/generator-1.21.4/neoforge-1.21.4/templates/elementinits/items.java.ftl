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
<#include "../procedures.java.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasBlocks = false>
<#assign hasDoubleBlocks = false>
<#assign hasItemsWithProperties = w.getGElementsOfType("item")?filter(e -> e.customProperties?has_content)?size != 0
	|| w.getGElementsOfType("tool")?filter(e -> e.toolType == "Shield")?size != 0>
<#assign itemsWithInventory = w.getGElementsOfType("item")?filter(e -> e.hasInventory())>

<#if itemsWithInventory?size != 0>
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
</#if>
public class ${JavaModName}Items {

	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(${JavaModName}.MODID);

	<#list items as item>
		<#if item.getModElement().getTypeString() == "armor">
			<#if item.enableHelmet>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_HELMET =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_helmet", ${item.getModElement().getName()}Item.Helmet::new, new Item.Properties());
			</#if>
			<#if item.enableBody>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_CHESTPLATE =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_chestplate", ${item.getModElement().getName()}Item.Chestplate::new, new Item.Properties());
			</#if>
			<#if item.enableLeggings>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_LEGGINGS =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_leggings", ${item.getModElement().getName()}Item.Leggings::new, new Item.Properties());
			</#if>
			<#if item.enableBoots>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_BOOTS =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_boots", ${item.getModElement().getName()}Item.Boots::new, new Item.Properties());
			</#if>
		<#elseif item.getModElement().getTypeString() == "livingentity">
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_SPAWN_EGG =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_spawn_egg",
						properties -> new SpawnEggItem(${JavaModName}Entities.${item.getModElement().getRegistryNameUpper()}.get(), properties), new Item.Properties());
		<#elseif item.getModElement().getTypeString() == "dimension" && item.hasIgniter()>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()} =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new, new Item.Properties());
		<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_BUCKET =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}_bucket", ${item.getModElement().getName()}Item::new, new Item.Properties());
		<#elseif item.getModElement().getTypeString() == "block" || item.getModElement().getTypeString() == "plant">
			<#if item.isDoubleBlock()>
				<#assign hasDoubleBlocks = true>
				public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()} = doubleBlock(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()});
			<#else>
				<#assign hasBlocks = true>
				public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()} = block(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()});
			</#if>
		<#else>
			public static final DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()} =
				REGISTRY.registerItem("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new, new Item.Properties());
		</#if>
	</#list>

	// Start of user code block custom items
	// End of user code block custom items

	<#if itemsWithInventory?size != 0>
	<#compress>
	@SubscribeEvent public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		<#list itemsWithInventory as item>
			event.registerItem(
				Capabilities.ItemHandler.ITEM,
				(stack, context) -> new ${item.getModElement().getName()}InventoryCapability(stack),
				${item.getModElement().getRegistryNameUpper()}.get()
			);
		</#list>
	}
	</#compress>
	</#if>

	<#if hasBlocks>
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.registerItem(block.getId().getPath(), properties -> new BlockItem(block.get(), properties), new Item.Properties());
	}
	</#if>

	<#if hasDoubleBlocks>
	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return REGISTRY.registerItem(block.getId().getPath(), properties -> new DoubleHighBlockItem(block.get(), properties), new Item.Properties());
	}
	</#if>

	<#if hasItemsWithProperties>
	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public static class ItemsClientSideHandler {
		@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void clientLoad(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
			<#compress>
			<#list items as item>
				<#if item.getModElement().getTypeString() == "item">
					<#list item.customProperties.entrySet() as property>
					ItemProperties.register(${item.getModElement().getRegistryNameUpper()}.get(),
						ResourceLocation.parse("${modid}:${item.getModElement().getRegistryName()}_${property.getKey()}"),
						(itemStackToRender, clientWorld, entity, itemEntityId) ->
							<#if hasProcedure(property.getValue())>
								(float) <@procedureCode property.getValue(), {
									"x": "entity != null ? entity.getX() : 0",
									"y": "entity != null ? entity.getY() : 0",
									"z": "entity != null ? entity.getZ() : 0",
									"world": "entity != null ? entity.level() : clientWorld",
									"entity": "entity",
									"itemstack": "itemStackToRender"
								}, false/>
							<#else>0</#if>
					);
					</#list>
				<#elseif item.getModElement().getTypeString() == "tool" && item.toolType == "Shield">
					ItemProperties.register(${item.getModElement().getRegistryNameUpper()}.get(), ResourceLocation.parse("minecraft:blocking"),
						ItemProperties.getProperty(new ItemStack(Items.SHIELD), ResourceLocation.parse("minecraft:blocking")));
				</#if>
			</#list>
			</#compress>
			});
		}
	}
	</#if>

}

<#-- @formatter:on -->