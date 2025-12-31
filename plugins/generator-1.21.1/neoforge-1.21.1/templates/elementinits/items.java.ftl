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
<#assign buckets = w.getGElementsOfType("fluid")?filter(e -> e.generateBucket)>

<#assign chunks = items?chunk(2500)>
<#assign has_chunks = chunks?size gt 1>

<#if itemsWithInventory?size != 0 || buckets?size != 0>
@EventBusSubscriber
</#if>
public class ${JavaModName}Items {

	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(${JavaModName}.MODID);

	<@javacompress>
	<#list items as item>
		<#if item.getModElement().getTypeString() == "armor">
			<#if item.enableHelmet>public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_HELMET;</#if>
			<#if item.enableBody>public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_CHESTPLATE;</#if>
			<#if item.enableLeggings>public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_LEGGINGS;</#if>
			<#if item.enableBoots>public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_BOOTS;</#if>
		<#elseif item.getModElement().getTypeString() == "livingentity">
			public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_SPAWN_EGG;
		<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
			public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()}_BUCKET;
		<#else>
			public static <#if !has_chunks>final</#if> DeferredItem<Item> ${item.getModElement().getRegistryNameUpper()};
		</#if>
	</#list>
	</@javacompress>

	<#list chunks as sub_items>
	<#if has_chunks>public static void register${sub_items?index}()<#else>static</#if> {
		<#list sub_items as item>
			<#if item.getModElement().getTypeString() == "armor">
				<#if item.enableHelmet>
				${item.getModElement().getRegistryNameUpper()}_HELMET =
					REGISTRY.register("${item.getModElement().getRegistryName()}_helmet", ${item.getModElement().getName()}Item.Helmet::new);
				</#if>
				<#if item.enableBody>
				${item.getModElement().getRegistryNameUpper()}_CHESTPLATE =
					REGISTRY.register("${item.getModElement().getRegistryName()}_chestplate", ${item.getModElement().getName()}Item.Chestplate::new);
				</#if>
				<#if item.enableLeggings>
				${item.getModElement().getRegistryNameUpper()}_LEGGINGS =
					REGISTRY.register("${item.getModElement().getRegistryName()}_leggings", ${item.getModElement().getName()}Item.Leggings::new);
				</#if>
				<#if item.enableBoots>
				${item.getModElement().getRegistryNameUpper()}_BOOTS =
					REGISTRY.register("${item.getModElement().getRegistryName()}_boots", ${item.getModElement().getName()}Item.Boots::new);
				</#if>
			<#elseif item.getModElement().getTypeString() == "livingentity">
				${item.getModElement().getRegistryNameUpper()}_SPAWN_EGG =
					REGISTRY.register("${item.getModElement().getRegistryName()}_spawn_egg",
						() -> new DeferredSpawnEggItem(${JavaModName}Entities.${item.getModElement().getRegistryNameUpper()},
						${item.spawnEggBaseColor.getRGB()}, ${item.spawnEggDotColor.getRGB()}, new Item.Properties()));
			<#elseif item.getModElement().getTypeString() == "specialentity">
				${item.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${item.getModElement().getRegistryName()}",
						() -> new BoatItem(<#if item.entityType == "Boat">false<#else>true</#if>,
						${JavaModName}BoatTypes.${item.getModElement().getRegistryNameUpper()}_TYPE.getValue(),
						new Item.Properties().stacksTo(1)));
			<#elseif item.getModElement().getTypeString() == "dimension" && item.hasIgniter()>
				${item.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
				${item.getModElement().getRegistryNameUpper()}_BUCKET =
					REGISTRY.register("${item.getModElement().getRegistryName()}_bucket", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "block" || item.getModElement().getTypeString() == "plant">
				<#if item.isDoubleBlock()>
					<#assign hasDoubleBlocks = true>
					${item.getModElement().getRegistryNameUpper()} =
					doubleBlock(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()}
					<#if item.hasCustomItemProperties()>, <@blockItemProperties item/></#if>);
				<#else>
					<#assign hasBlocks = true>
					${item.getModElement().getRegistryNameUpper()} =
					block(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()}
					<#if item.hasCustomItemProperties()>, <@blockItemProperties item/></#if>);
				</#if>
			<#else>
				${item.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
			</#if>
		</#list>
	}
	</#list>

	<#if has_chunks>
	static {
		<#list 0..chunks?size-1 as i>register${i}();</#list>
	}
	</#if>

	// Start of user code block custom items
	// End of user code block custom items

	<#if itemsWithInventory?size != 0 || buckets?size != 0>
	<@javacompress>
	@SubscribeEvent public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		<#list itemsWithInventory as item>
			event.registerItem(
				Capabilities.ItemHandler.ITEM,
				(stack, context) -> new ${item.getModElement().getName()}InventoryCapability(stack),
				${item.getModElement().getRegistryNameUpper()}.get()
			);
		</#list>
		<#list buckets as item>
			event.registerItem(
				Capabilities.FluidHandler.ITEM,
				(stack, context) -> new FluidBucketWrapper(stack),
				${item.getModElement().getRegistryNameUpper()}_BUCKET.get()
			);
		</#list>
	}
	</@javacompress>
	</#if>

	<#if hasBlocks>
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
	</#if>

	<#if hasDoubleBlocks>
	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return doubleBlock(block, new Item.Properties());
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), properties));
	}
	</#if>

	<#if hasItemsWithProperties>
	@EventBusSubscriber(Dist.CLIENT) public static class ItemsClientSideHandler {
		@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void clientLoad(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
			<@javacompress>
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
			</@javacompress>
			});
		}
	}
	</#if>

}

<#macro blockItemProperties block>
new Item.Properties()
<#if block.maxStackSize != 64>
	.stacksTo(${block.maxStackSize})
</#if>
<#if block.rarity != "COMMON">
	.rarity(Rarity.${block.rarity})
</#if>
<#if block.immuneToFire>
	.fireResistant()
</#if>
</#macro>
<#-- @formatter:on -->