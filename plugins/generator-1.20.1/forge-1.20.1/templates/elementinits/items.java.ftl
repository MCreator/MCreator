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
<#include "../procedures.java.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign hasBlocks = false>
<#assign hasDoubleBlocks = false>
<#assign hasItemsWithProperties = w.getGElementsOfType("item")?filter(e -> e.customProperties?has_content)?size != 0
	|| w.getGElementsOfType("tool")?filter(e -> e.toolType == "Shield")?size != 0>

<#if hasItemsWithProperties>
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
</#if>
public class ${JavaModName}Items {

	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ${JavaModName}.MODID);

	<#list items as item>
		<#if item.getModElement().getTypeString() == "armor">
			<#if item.enableHelmet>public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_HELMET;</#if>
			<#if item.enableBody>public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_CHESTPLATE;</#if>
			<#if item.enableLeggings>public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_LEGGINGS;</#if>
			<#if item.enableBoots>public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_BOOTS;</#if>
		<#elseif item.getModElement().getTypeString() == "livingentity">
			public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_SPAWN_EGG;
		<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
			public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()}_BUCKET;
		<#else>
			public static RegistryObject<Item> ${item.getModElement().getRegistryNameUpper()};
		</#if>
	</#list>

	<#assign chunks = items?chunk(2500)>
	<#assign chunks_num = chunks?size>
	<#list chunks as sub_items>
	public static void register<#if chunks_num == 1>(IEventBus modEventBus)<#else>${sub_items?index}()</#if> {
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
						() -> new ForgeSpawnEggItem(${JavaModName}Entities.${item.getModElement().getRegistryNameUpper()},
						${item.spawnEggBaseColor.getRGB()}, ${item.spawnEggDotColor.getRGB()}, new Item.Properties()));
			<#elseif item.getModElement().getTypeString() == "dimension" && item.hasIgniter()>
				${item.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "fluid" && item.generateBucket>
				${item.getModElement().getRegistryNameUpper()}_BUCKET =
					REGISTRY.register("${item.getModElement().getRegistryName()}_bucket", ${item.getModElement().getName()}Item::new);
			<#elseif item.getModElement().getTypeString() == "block" || item.getModElement().getTypeString() == "plant">
				<#if item.isDoubleBlock()>
					<#assign hasDoubleBlocks = true>
					${item.getModElement().getRegistryNameUpper()} = doubleBlock(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()});
				<#else>
					<#assign hasBlocks = true>
					${item.getModElement().getRegistryNameUpper()} = block(${JavaModName}Blocks.${item.getModElement().getRegistryNameUpper()});
				</#if>
			<#else>
				${item.getModElement().getRegistryNameUpper()} =
					REGISTRY.register("${item.getModElement().getRegistryName()}", ${item.getModElement().getName()}Item::new);
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

	// Start of user code block custom items
	// End of user code block custom items

	<#if hasBlocks>
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
	</#if>

	<#if hasDoubleBlocks>
	private static RegistryObject<Item> doubleBlock(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
	</#if>

	<#if hasItemsWithProperties>
	<#compress>
	@SubscribeEvent public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
		<#list items as item>
			<#if item.getModElement().getTypeString() == "item">
				<#list item.customProperties.entrySet() as property>
				ItemProperties.register(${item.getModElement().getRegistryNameUpper()}.get(),
					new ResourceLocation("${modid}:${item.getModElement().getRegistryName()}_${property.getKey()}"),
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
				ItemProperties.register(${item.getModElement().getRegistryNameUpper()}.get(), new ResourceLocation("blocking"),
					ItemProperties.getProperty(Items.SHIELD, new ResourceLocation("blocking")));
			</#if>
		</#list>
		});
	}
	</#compress>
	</#if>

}

<#-- @formatter:on -->