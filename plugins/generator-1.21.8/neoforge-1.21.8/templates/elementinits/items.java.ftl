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
<#assign hasItemsWithCustomProperties = w.getGElementsOfType("item")?filter(e -> e.customProperties?has_content)?size != 0>
<#assign hasItemsWithLeftHandedProperty = w.getGElementsOfType("item")?filter(e -> e.states
	?filter(e -> e.stateMap.keySet()?filter(e -> e.getName() == "lefthanded")?size != 0)?size != 0)?size != 0>
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
					register("${item.getModElement().getRegistryName()}_helmet", ${item.getModElement().getName()}Item.Helmet::new);
				</#if>
				<#if item.enableBody>
				${item.getModElement().getRegistryNameUpper()}_CHESTPLATE =
					register("${item.getModElement().getRegistryName()}_chestplate", ${item.getModElement().getName()}Item.Chestplate::new);
				</#if>
				<#if item.enableLeggings>
				${item.getModElement().getRegistryNameUpper()}_LEGGINGS =
					register("${item.getModElement().getRegistryName()}_leggings", ${item.getModElement().getName()}Item.Leggings::new);
				</#if>
				<#if item.enableBoots>
				${item.getModElement().getRegistryNameUpper()}_BOOTS =
					register("${item.getModElement().getRegistryName()}_boots", ${item.getModElement().getName()}Item.Boots::new);
				</#if>
			<#elseif item.getModElement().getTypeString() == "livingentity">
				${item.getModElement().getRegistryNameUpper()}_SPAWN_EGG =
					register("${item.getModElement().getRegistryName()}_spawn_egg",
						properties -> new SpawnEggItem(${JavaModName}Entities.${item.getModElement().getRegistryNameUpper()}.get(), properties));
			<#elseif item.getModElement().getTypeString() == "specialentity">
				${item.getModElement().getRegistryNameUpper()} =
					register("${item.getModElement().getRegistryName()}",
						properties -> new BoatItem(${JavaModName}Entities.${item.getModElement().getRegistryNameUpper()}.get(), properties.stacksTo(1)));
			<#elseif item.getModElement().getTypeString() == "dimension" && item.hasIgniter()>
				${item.getModElement().getRegistryNameUpper()} =
					register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
				${item.getModElement().getRegistryNameUpper()}_BUCKET =
					register("${item.getModElement().getRegistryName()}_bucket", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "block" || item.getModElement().getTypeString() == "plant">
				<#if item.hasSpecialInformation(w)>
					${item.getModElement().getRegistryNameUpper()} =
						register("${item.getModElement().getRegistryName()}",
							<#if item.hasCustomItemProperties()>
								properties -> new ${item.getModElement().getName()}Block.Item(<@blockItemProperties item false/>)
							<#else>
								${item.getModElement().getName()}Block.Item::new
							</#if>
						);
				<#else>
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
				</#if>
			<#else>
				${item.getModElement().getRegistryNameUpper()} =
					register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
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

	private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> supplier) {
		return REGISTRY.registerItem(name, supplier, new Item.Properties());
	}

	<#if hasBlocks>
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.registerItem(block.getId().getPath(), prop -> new BlockItem(block.get(), prop), properties);
	}
	</#if>

	<#if hasDoubleBlocks>
	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return doubleBlock(block, new Item.Properties());
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.registerItem(block.getId().getPath(), prop -> new DoubleHighBlockItem(block.get(), prop), properties);
	}
	</#if>

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

	<#if hasItemsWithCustomProperties || hasItemsWithLeftHandedProperty>
	@EventBusSubscriber(Dist.CLIENT) public static class ItemsClientSideHandler {

		<#if hasItemsWithCustomProperties>
		@SubscribeEvent public static void registerItemModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
			<@javacompress>
			<#list items as item>
				<#if item.getModElement().getTypeString() == "item">
					<#list item.customProperties.entrySet() as property>
					event.register(ResourceLocation.parse("${modid}:${item.getModElement().getRegistryName()}/${property.getKey()}"),
						${item.getModElement().getName()}Item.${StringUtils.snakeToCamel(property.getKey())}Property.MAP_CODEC);
					</#list>
				</#if>
			</#list>
			</@javacompress>
		}
		</#if>

		<#if hasItemsWithLeftHandedProperty>
		@SubscribeEvent public static void registerItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
			event.register(ResourceLocation.parse("${modid}:lefthanded"), LegacyLeftHandedProperty.MAP_CODEC);
		}

		public record LegacyLeftHandedProperty() implements ConditionalItemModelProperty {

			public static final MapCodec<LegacyLeftHandedProperty> MAP_CODEC = MapCodec.unit(new LegacyLeftHandedProperty());

			@Override
			public boolean get(ItemStack itemStackToRender, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
				return entity != null && entity.getMainArm() == HumanoidArm.LEFT;
			}

			@Override
			public MapCodec<LegacyLeftHandedProperty> type() {
				return MAP_CODEC;
			}
		}
		</#if>

	}
	</#if>

}

<#macro blockItemProperties block newProperties=true>
<#if newProperties>new Item.Properties()<#else>properties</#if>
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