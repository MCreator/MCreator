<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
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

package ${package}.item;

@${JavaModName}Elements.ModElement.Tag public class ${name}Item extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}_helmet")
	public static final Item helmet = null;

	@ObjectHolder("${modid}:${registryname}_chestplate")
	public static final Item body = null;

	@ObjectHolder("${modid}:${registryname}_leggings")
	public static final Item legs = null;

	@ObjectHolder("${modid}:${registryname}_boots")
	public static final Item boots = null;

	public ${name}Item (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		IArmorMaterial armormaterial = new IArmorMaterial() {
			public int getDurability(EquipmentSlotType slot) {
				return new int[]{13, 15, 16, 11}[slot.getIndex()] * ${data.maxDamage};
			}

  		 	public int getDamageReductionAmount(EquipmentSlotType slot) {
				return new int[] { ${data.damageValueBoots}, ${data.damageValueLeggings}, ${data.damageValueBody}, ${data.damageValueHelmet} }[slot.getIndex()];
			}

			public int getEnchantability() {
				return ${data.enchantability};
			}

			public net.minecraft.util.SoundEvent getSoundEvent() {
				<#if data.equipSound??>
				return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.equipSound}"));
				<#else>
				return null;
				</#if>
			}

			public Ingredient getRepairMaterial() {
				<#if data.repairItems?has_content>
				return Ingredient.fromStacks(
							<#list data.repairItems as repairItem>
							${mappedMCItemToItemStackCode(repairItem,1)}<#if repairItem?has_next>,</#if>
                			</#list>
						);
				<#else>
				return Ingredient.EMPTY;
				</#if>
			}

			@OnlyIn(Dist.CLIENT)
			public String getName() {
				return 	"${registryname}";
			}

			public float getToughness() {
				return 	${data.toughness}f;
			}
		};

		<#if data.enableHelmet>
        elements.items.add(() ->
			new ArmorItem(armormaterial, EquipmentSlotType.HEAD, new Item.Properties()<#if data.enableHelmet>.group(${data.creativeTab})</#if>) {
				<#if data.helmetModelName != "Default">
				@Override @OnlyIn(Dist.CLIENT) public BipedModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlotType slot, BipedModel defaultModel) {
					BipedModel armorModel = new BipedModel();
					armorModel.bipedHead = new ${data.helmetModelName}().${data.helmetModelPart};
					armorModel.isSneak = living.isSneaking();
					armorModel.isSitting = defaultModel.isSitting;
					armorModel.isChild = living.isChild();
					return armorModel;
				}
				</#if>

				<#if data.helmetSpecialInfo?has_content || data.helmetShiftInfo?has_content || data.helmetCommandInfo?has_content>
				@Override @OnlyIn(Dist.CLIENT) public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
					super.addInformation(itemstack, world, list, flag);
					<#if data.helmetSpecialInfo?has_content>
					<#assign line = 1>
					<#list data.helmetSpecialInfo as entry>
					list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_helmet.tooltip${line}"));
					<#assign line++>
					</#list>
					</#if>
					<#if data.helmetShiftInfo?has_content && data.armorHelmetShiftOnly()>
					if (Screen.hasShiftDown()) {
						<#assign line = 1>
						<#list data.helmetShiftInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_helmet.shift.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press SHIFT for more information"));
					}
					</#if>
					<#if data.helmetCommandInfo?has_content && data.armorHelmetCommandOnly()>
					if (Screen.hasControlDown()) {
						<#assign line = 1>
						<#list data.helmetCommandInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_helmet.command.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press CTRL for more information"));
					}
					</#if>
				}
				</#if>

				@Override public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
					<#if data.helmetModelTexture?has_content && data.helmetModelTexture != "From armor">
					return "${modid}:textures/${data.helmetModelTexture}";
					<#else>
					return "${modid}:textures/models/armor/${data.armorTextureFile}_layer_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
					</#if>
				}

				<#if hasProcedure(data.onHelmetTick)>
				@Override public void onArmorTick(ItemStack itemstack, World world, PlayerEntity entity) {
					super.onArmorTick(itemstack, world, entity);
					double x = entity.posX;
					double y = entity.posY;
					double z = entity.posZ;
					<@procedureOBJToCode data.onHelmetTick/>
				}
				</#if>
		}.setRegistryName("${registryname}_helmet"));
        </#if>

        <#if data.enableBody>
        elements.items.add(() ->
			new ArmorItem(armormaterial, EquipmentSlotType.CHEST, new Item.Properties()<#if data.enableBody>.group(${data.creativeTab})</#if>) {
				<#if data.bodyModelName != "Default">
				@Override @OnlyIn(Dist.CLIENT) public BipedModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlotType slot, BipedModel defaultModel) {
					BipedModel armorModel = new BipedModel();
					armorModel.bipedBody = new ${data.bodyModelName}().${data.bodyModelPart};

					<#if data.armsModelPartL?has_content>
					armorModel.bipedLeftArm = new ${data.bodyModelName}().${data.armsModelPartL};
					</#if>
					<#if data.armsModelPartR?has_content>
					armorModel.bipedRightArm = new ${data.bodyModelName}().${data.armsModelPartR};
					</#if>

					armorModel.isSneak = living.isSneaking();
					armorModel.isSitting = defaultModel.isSitting;
					armorModel.isChild = living.isChild();
					return armorModel;
				}
				</#if>

				<#if data.bodySpecialInfo?has_content || data.bodyShiftInfo?has_content || data.bodyCommandInfo?has_content>
				@Override @OnlyIn(Dist.CLIENT) public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
					super.addInformation(itemstack, world, list, flag);
					<#if data.bodySpecialInfo?has_content>
					<#assign line = 1>
					<#list data.bodySpecialInfo as entry>
					list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_chestplate.tooltip${line}"));
					<#assign line++>
					</#list>
					</#if>
					<#if data.bodyShiftInfo?has_content && data.armorChestplateShiftOnly()>
					if (Screen.hasShiftDown()) {
						<#assign line = 1>
						<#list data.bodyShiftInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_chestplate.shift.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press SHIFT for more information"));
					}
					</#if>
					<#if data.bodyCommandInfo?has_content && data.armorChestplateCommandOnly()>
					if (Screen.hasControlDown()) {
						<#assign line = 1>
						<#list data.bodyCommandInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_chestplate.command.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press CTRL for more information"));
					}
					</#if>
				}
				</#if>

				@Override public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
					<#if data.bodyModelTexture?has_content && data.bodyModelTexture != "From armor">
					return "${modid}:textures/${data.bodyModelTexture}";
					<#else>
					return "${modid}:textures/models/armor/${data.armorTextureFile}_layer_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
					</#if>
				}

				<#if hasProcedure(data.onBodyTick)>
				@Override public void onArmorTick(ItemStack itemstack, World world, PlayerEntity entity) {
					double x = entity.posX;
					double y = entity.posY;
					double z = entity.posZ;
					<@procedureOBJToCode data.onBodyTick/>
				}
				</#if>
		}.setRegistryName("${registryname}_chestplate"));
        </#if>

        <#if data.enableLeggings>
        elements.items.add(() ->
			new ArmorItem(armormaterial, EquipmentSlotType.LEGS, new Item.Properties()<#if data.enableLeggings>.group(${data.creativeTab})</#if>) {
				<#if data.leggingsModelName != "Default">
				@Override @OnlyIn(Dist.CLIENT) public BipedModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlotType slot, BipedModel defaultModel) {
					BipedModel armorModel = new BipedModel();
					armorModel.bipedLeftLeg = new ${data.leggingsModelName}().${data.leggingsModelPartL};
					armorModel.bipedRightLeg = new ${data.leggingsModelName}().${data.leggingsModelPartR};
					armorModel.isSneak = living.isSneaking();
					armorModel.isSitting = defaultModel.isSitting;
					armorModel.isChild = living.isChild();
					return armorModel;
				}
				</#if>

				<#if data.leggingsSpecialInfo?has_content || data.leggingsShiftInfo?has_content || data.leggingsCommandInfo?has_content>
				@Override @OnlyIn(Dist.CLIENT) public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
					super.addInformation(itemstack, world, list, flag);
					<#if data.leggingsSpecialInfo?has_content>
					<#assign line = 1>
					<#list data.leggingsSpecialInfo as entry>
					list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_leggings.tooltip${line}"));
					<#assign line++>
					</#list>
					</#if>
					<#if data.leggingsShiftInfo?has_content && data.armorLeggingsShiftOnly()>
					if (Screen.hasShiftDown()) {
						<#assign line = 1>
						<#list data.leggingsShiftInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_leggings.shift.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press SHIFT for more information"));
					}
					</#if>
					<#if data.leggingsCommandInfo?has_content && data.armorLeggingsCommandOnly()>
					if (Screen.hasControlDown()) {
						<#assign line = 1>
						<#list data.leggingsCommandInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_leggings.command.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press CTRL for more information"));
					}
					</#if>
				}
				</#if>

				@Override public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
					<#if data.leggingsModelTexture?has_content && data.leggingsModelTexture != "From armor">
					return "${modid}:textures/${data.leggingsModelTexture}";
					<#else>
					return "${modid}:textures/models/armor/${data.armorTextureFile}_layer_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
					</#if>
				}

				<#if hasProcedure(data.onLeggingsTick)>
				@Override public void onArmorTick(ItemStack itemstack, World world, PlayerEntity entity) {
					double x = entity.posX;
					double y = entity.posY;
					double z = entity.posZ;
					<@procedureOBJToCode data.onLeggingsTick/>
				}
				</#if>
		}.setRegistryName("${registryname}_leggings"));
        </#if>

        <#if data.enableBoots>
        elements.items.add(() ->
			new ArmorItem(armormaterial, EquipmentSlotType.FEET, new Item.Properties()<#if data.enableBoots>.group(${data.creativeTab})</#if>) {
				<#if data.bootsModelName != "Default">
				@Override @OnlyIn(Dist.CLIENT) public BipedModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlotType slot, BipedModel defaultModel) {
					BipedModel armorModel = new BipedModel();
					armorModel.bipedLeftLeg = new ${data.bootsModelName}().${data.bootsModelPartL};
					armorModel.bipedRightLeg = new ${data.bootsModelName}().${data.bootsModelPartR};
					armorModel.isSneak = living.isSneaking();
					armorModel.isSitting = defaultModel.isSitting;
					armorModel.isChild = living.isChild();
					return armorModel;
				}
				</#if>

				<#if data.bootsSpecialInfo?has_content || data.bootsShiftInfo?has_content || data.bootsCommandInfo?has_content>
				@Override @OnlyIn(Dist.CLIENT) public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
					super.addInformation(itemstack, world, list, flag);
					<#if data.bootsSpecialInfo?has_content>
					<#assign line = 1>
					<#list data.bootsSpecialInfo as entry>
					list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_boots.tooltip${line}"));
					<#assign line++>
					</#list>
					</#if>
					<#if data.bootsShiftInfo?has_content && data.armorBootsShiftOnly()>
					if (Screen.hasShiftDown()) {
						<#assign line = 1>
						<#list data.bootsShiftInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_boots.shift.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press SHIFT for more information"));
					}
					</#if>
					<#if data.bootsCommandInfo?has_content && data.armorBootsCommandOnly()>
					if (Screen.hasControlDown()) {
						<#assign line = 1>
						<#list data.bootsCommandInfo as entry>
						list.add(new TranslationTextComponent("item.${modid?lower_case}.${registryname?lower_case}_boots.command.tooltip${line}"));
						<#assign line++>
						</#list>
					} else {
						list.add(new StringTextComponent("\u00A77Press CTRL for more information"));
					}
					</#if>
				}
				</#if>

				@Override public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
					<#if data.bootsModelTexture?has_content && data.bootsModelTexture != "From armor">
					return "${modid}:textures/${data.bootsModelTexture}";
					<#else>
					return "${modid}:textures/models/armor/${data.armorTextureFile}_layer_" + (slot == EquipmentSlotType.LEGS ? "2" : "1") + ".png";
					</#if>
				}

				<#if hasProcedure(data.onBootsTick)>
				@Override public void onArmorTick(ItemStack itemstack, World world, PlayerEntity entity) {
					double x = entity.posX;
					double y = entity.posY;
					double z = entity.posZ;
					<@procedureOBJToCode data.onBootsTick/>
				}
				</#if>
		}.setRegistryName("${registryname}_boots"));
        </#if>
	}

	${data.getArmorModelsCode().toString()
		.replace("ModelRenderer", "RendererModel").replace("extends ModelBase", "extends EntityModel<Entity>")
		.replace("GlStateManager.translate", "GlStateManager.translated")
		.replace("GlStateManager.scale", "GlStateManager.scaled")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+e\\)",
					"setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4, float f5)")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+entity\\)",
					"setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)")
		.replaceAll("setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+e\\)", "setRotationAngles(e, f, f1, f2, f3, f4, f5)")
		.replaceAll("setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+entity\\)", "setRotationAngles(entity, f, f1, f2, f3, f4, f5)")
	}

}
<#-- @formatter:on -->