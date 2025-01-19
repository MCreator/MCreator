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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#include "triggers.java.ftl">

package ${package}.item;

import java.util.Map;import java.util.function.Consumer;
import net.minecraft.client.model.Model;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public abstract class ${name}Item extends ArmorItem {

	public static ArmorMaterial ARMOR_MATERIAL = new ArmorMaterial(
		${data.maxDamage},
		Map.of(
			ArmorType.BOOTS, ${data.damageValueBoots},
			ArmorType.LEGGINGS, ${data.damageValueLeggings},
			ArmorType.CHESTPLATE, ${data.damageValueBody},
			ArmorType.HELMET, ${data.damageValueHelmet},
			ArmorType.BODY, ${data.damageValueBody}
		),
		${data.enchantability},
		<#if data.equipSound?has_content && data.equipSound.getUnmappedValue()?has_content>
		DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("${data.equipSound}")),
		<#else>
		BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
		</#if>
		${data.toughness}f,
		${data.knockbackResistance}f,
		TagKey.create(Registries.ITEM, ResourceLocation.parse("${modid}:${registryname}_repair_items")), <#-- data.repairItems are put into a tag -->
		ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.parse("${modid}:${registryname}")) <#-- data.armorTextureFile - just dummy, we override this in client extensions -->
	);

	@SubscribeEvent public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
		<#if data.enableHelmet>
		event.registerItem(new IClientItemExtensions() {
			<#if data.helmetModelName != "Default" && data.getHelmetModel()??>
			@Override public HumanoidModel getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
				ModelPart head = new ${data.helmetModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.helmetModelName}.LAYER_LOCATION)).${data.helmetModelPart};
				head.setInitialPose(PartPose.rotation(0, (float) (Math.PI), 0));
				head.resetPose();
				return new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of(
					"head", new ModelPart(Collections.emptyList(), Map.of(
						"head", head,
						"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap())
					)),
					"body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap())
				)));
			}
			</#if>

			@Override public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
				<#if data.helmetModelTexture?has_content && data.helmetModelTexture != "From armor">
				return ResourceLocation.parse("${modid}:textures/entities/${data.helmetModelTexture}");
				<#else>
				return ResourceLocation.parse("${modid}:textures/models/armor/${data.armorTextureFile}_layer_1.png");
				</#if>
			}
		}, ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_HELMET.get());
		</#if>

		<#if data.enableBody>
		event.registerItem(new IClientItemExtensions() {
			<#if data.bodyModelName != "Default" && data.getBodyModel()??>
			@Override @OnlyIn(Dist.CLIENT) public HumanoidModel getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
				return new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of(
					"body", new ${data.bodyModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.bodyModelName}.LAYER_LOCATION)).${data.bodyModelPart},
					"left_arm", new ${data.bodyModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.bodyModelName}.LAYER_LOCATION)).${data.armsModelPartL},
					"right_arm", new ${data.bodyModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.bodyModelName}.LAYER_LOCATION)).${data.armsModelPartR},
					"head", new ModelPart(Collections.emptyList(), Map.of(
						"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap())
					)),
					"right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap())
				)));
			}
			</#if>

			@Override public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
				<#if data.bodyModelTexture?has_content && data.bodyModelTexture != "From armor">
				return ResourceLocation.parse("${modid}:textures/entities/${data.bodyModelTexture}");
				<#else>
				return ResourceLocation.parse("${modid}:textures/models/armor/${data.armorTextureFile}_layer_1.png");
				</#if>
			}
		}, ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_CHESTPLATE.get());
		</#if>

		<#if data.enableLeggings>
		event.registerItem(new IClientItemExtensions() {
			<#if data.leggingsModelName != "Default" && data.getLeggingsModel()??>
			@Override @OnlyIn(Dist.CLIENT) public HumanoidModel getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
				return new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of(
					"left_leg", new ${data.leggingsModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.leggingsModelName}.LAYER_LOCATION)).${data.leggingsModelPartL},
					"right_leg", new ${data.leggingsModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.leggingsModelName}.LAYER_LOCATION)).${data.leggingsModelPartR},
					"head", new ModelPart(Collections.emptyList(), Map.of(
						"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap())
					)),
					"body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap())
				)));
			}
			</#if>

			@Override public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
				<#if data.leggingsModelTexture?has_content && data.leggingsModelTexture != "From armor">
				return ResourceLocation.parse("${modid}:textures/entities/${data.leggingsModelTexture}");
				<#else>
				return ResourceLocation.parse("${modid}:textures/models/armor/${data.armorTextureFile}_layer_2.png");
				</#if>
			}
		}, ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_LEGGINGS.get());
		</#if>

		<#if data.enableBoots>
		event.registerItem(new IClientItemExtensions() {
			<#if data.bootsModelName != "Default" && data.getBootsModel()??>
			@Override @OnlyIn(Dist.CLIENT) public HumanoidModel getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
				return new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of(
					"left_leg", new ${data.bootsModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.bootsModelName}.LAYER_LOCATION)).${data.bootsModelPartL},
					"right_leg", new ${data.bootsModelName}(Minecraft.getInstance().getEntityModels().bakeLayer(${data.bootsModelName}.LAYER_LOCATION)).${data.bootsModelPartR},
					"head", new ModelPart(Collections.emptyList(), Map.of(
						"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap())
					)),
					"body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
					"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap())
				)));
			}
			</#if>

			@Override public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
				<#if data.bootsModelTexture?has_content && data.bootsModelTexture != "From armor">
				return ResourceLocation.parse("${modid}:textures/entities/${data.bootsModelTexture}");
				<#else>
				return ResourceLocation.parse("${modid}:textures/models/armor/${data.armorTextureFile}_layer_1.png");
				</#if>
			}
		}, ${JavaModName}Items.${data.getModElement().getRegistryNameUpper()}_BOOTS.get());
		</#if>
	}

	private ${name}Item(ArmorType type, Item.Properties properties) {
		super(ARMOR_MATERIAL, type, properties);
	}

	<#if data.enableHelmet>
	public static class Helmet extends ${name}Item {

		public Helmet(Item.Properties properties) {
			super(ArmorType.HELMET, properties<#if data.helmetImmuneToFire>.fireResistant()</#if>);
		}

		<@addSpecialInformation data.helmetSpecialInformation, "item." + modid + "." + registryname + "_helmet"/>

		<@piglinNeutral data.helmetPiglinNeutral/>

		<@onArmorTick data.onHelmetTick/>
	}
	</#if>

	<#if data.enableBody>
	public static class Chestplate extends ${name}Item {

		public Chestplate(Item.Properties properties) {
			super(ArmorType.CHESTPLATE, properties<#if data.bodyImmuneToFire>.fireResistant()</#if>);
		}

		<@addSpecialInformation data.bodySpecialInformation, "item." + modid + "." + registryname + "_chestplate"/>

		<@piglinNeutral data.bodyPiglinNeutral/>

		<@onArmorTick data.onBodyTick/>
	}
	</#if>

	<#if data.enableLeggings>
	public static class Leggings extends ${name}Item {

		public Leggings(Item.Properties properties) {
			super(ArmorType.LEGGINGS, properties<#if data.leggingsImmuneToFire>.fireResistant()</#if>);
		}

		<@addSpecialInformation data.leggingsSpecialInformation, "item." + modid + "." + registryname + "_leggings"/>

		<@piglinNeutral data.leggingsPiglinNeutral/>

		<@onArmorTick data.onLeggingsTick/>
	}
	</#if>

	<#if data.enableBoots>
	public static class Boots extends ${name}Item {

		public Boots(Item.Properties properties) {
			super(ArmorType.BOOTS, properties<#if data.bootsImmuneToFire>.fireResistant()</#if>);
		}

		<@addSpecialInformation data.bootsSpecialInformation, "item." + modid + "." + registryname + "_boots"/>

		<@piglinNeutral data.bootsPiglinNeutral/>

		<@onArmorTick data.onBootsTick/>
	}
	</#if>

}
<#-- @formatter:on -->