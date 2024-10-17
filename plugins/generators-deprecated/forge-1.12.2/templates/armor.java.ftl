<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.item;

@Elements${JavaModName}.ModElement.Tag public class Item${name} extends Elements${JavaModName}.ModElement{

	@GameRegistry.ObjectHolder("${modid}:${registryname}helmet")
	public static final Item helmet = null;

	@GameRegistry.ObjectHolder("${modid}:${registryname}body")
	public static final Item body = null;

	@GameRegistry.ObjectHolder("${modid}:${registryname}legs")
	public static final Item legs = null;

	@GameRegistry.ObjectHolder("${modid}:${registryname}boots")
	public static final Item boots = null;

	public Item${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("${registryname?upper_case}" ,"${modid}:${data.armorTextureFile}" , ${data.maxDamage},
						new int[] { ${data.damageValueBoots}, ${data.damageValueLeggings}, ${data.damageValueBody}, ${data.damageValueHelmet} },
						${data.enchantability}, <#if data.equipSound??>(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation("${data.equipSound}" ))<#else>null</#if>, ${data.toughness}f);

		<#if data.enableHelmet>
        elements.items.add(() ->
			new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) <#if hasProcedure(data.onHelmetTick) || data.helmetModelName != "Default">{
				<#if data.helmetModelName != "Default">
				@Override @SideOnly(Side.CLIENT) public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
					ModelBiped armorModel = new ModelBiped();
					armorModel.bipedHead = new ${data.helmetModelName}().${data.helmetModelPart};
					armorModel.isSneak = living.isSneaking();
					armorModel.isRiding = living.isRiding();
					armorModel.isChild = living.isChild();
					return armorModel;
				}

				<#if data.helmetModelTexture != "From armor">
				@Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
					return "${modid}:textures/${data.helmetModelTexture}";
				}
				</#if>

				</#if>

				<#if hasProcedure(data.onHelmetTick)>
				@Override public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
					super.onArmorTick(world, entity, itemstack);
					int x = (int) entity.posX;
					int y = (int) entity.posY;
					int z = (int) entity.posZ;
					<@procedureOBJToCode data.onHelmetTick/>
				}
				</#if>
		}</#if>.setUnlocalizedName("${registryname}helmet").setRegistryName("${registryname}helmet")<#if data.enableHelmet>.setCreativeTab(${data.creativeTab})</#if>);
        </#if>

        <#if data.enableBody>
        elements.items.add(() ->
			new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) <#if hasProcedure(data.onBodyTick) || data.bodyModelName != "Default">{
				<#if data.bodyModelName != "Default">
				@Override @SideOnly(Side.CLIENT) public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
					ModelBiped armorModel = new ModelBiped();
					armorModel.bipedBody = new ${data.bodyModelName}().${data.bodyModelPart};
					armorModel.isSneak = living.isSneaking();
					armorModel.isRiding = living.isRiding();
					armorModel.isChild = living.isChild();
					return armorModel;
				}

				<#if data.bodyModelTexture != "From armor">
				@Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
					return "${modid}:textures/${data.bodyModelTexture}";
				}
				</#if>

				</#if>

				<#if hasProcedure(data.onBodyTick)>
				@Override public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
					int x = (int) entity.posX;
					int y = (int) entity.posY;
					int z = (int) entity.posZ;
					<@procedureOBJToCode data.onBodyTick/>
				}
				</#if>
		}</#if>.setUnlocalizedName("${registryname}body").setRegistryName("${registryname}body")<#if data.enableBody>.setCreativeTab(${data.creativeTab})</#if>);
        </#if>

        <#if data.enableLeggings>
        elements.items.add(() ->
			new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS) <#if hasProcedure(data.onLeggingsTick) || data.leggingsModelName != "Default">{
				<#if data.leggingsModelName != "Default">
				@Override @SideOnly(Side.CLIENT) public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
					ModelBiped armorModel = new ModelBiped();
					armorModel.bipedLeftLeg = new ${data.leggingsModelName}().${data.leggingsModelPartL};
					armorModel.bipedRightLeg = new ${data.leggingsModelName}().${data.leggingsModelPartR};
					armorModel.isSneak = living.isSneaking();
					armorModel.isRiding = living.isRiding();
					armorModel.isChild = living.isChild();
					return armorModel;
				}

				<#if data.leggingsModelTexture != "From armor">
				@Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
					return "${modid}:textures/${data.leggingsModelTexture}";
				}
				</#if>

				</#if>

				<#if hasProcedure(data.onLeggingsTick)>
				@Override public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
					int x = (int) entity.posX;
					int y = (int) entity.posY;
					int z = (int) entity.posZ;
					<@procedureOBJToCode data.onLeggingsTick/>
				}
				</#if>
		}</#if>.setUnlocalizedName("${registryname}legs").setRegistryName("${registryname}legs")<#if data.enableLeggings>.setCreativeTab(${data.creativeTab})</#if>);
        </#if>

        <#if data.enableBoots>
        elements.items.add(() ->
			new ItemArmor(enuma, 0, EntityEquipmentSlot.FEET) <#if hasProcedure(data.onBootsTick) || data.bootsModelName != "Default">{
				<#if data.bootsModelName != "Default">
				@Override @SideOnly(Side.CLIENT) public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
					ModelBiped armorModel = new ModelBiped();
					armorModel.bipedLeftLeg = new ${data.bootsModelName}().${data.bootsModelPartL};
					armorModel.bipedRightLeg = new ${data.bootsModelName}().${data.bootsModelPartR};
					armorModel.isSneak = living.isSneaking();
					armorModel.isRiding = living.isRiding();
					armorModel.isChild = living.isChild();
					return armorModel;
				}

				<#if data.bootsModelTexture != "From armor">
				@Override public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
					return "${modid}:textures/${data.bootsModelTexture}";
				}
				</#if>

				</#if>

				<#if hasProcedure(data.onBootsTick)>
				@Override public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
					int x = (int) entity.posX;
					int y = (int) entity.posY;
					int z = (int) entity.posZ;
					<@procedureOBJToCode data.onBootsTick/>
				}
				</#if>
		}</#if>.setUnlocalizedName("${registryname}boots").setRegistryName("${registryname}boots")<#if data.enableBoots>.setCreativeTab(${data.creativeTab})</#if>);
        </#if>
	}

	@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
		<#if data.enableHelmet>ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("${modid}:${registryname}helmet", "inventory"));</#if>
		<#if data.enableBody>ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("${modid}:${registryname}body", "inventory"));</#if>
		<#if data.enableLeggings>ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("${modid}:${registryname}legs", "inventory"));</#if>
		<#if data.enableBoots>ModelLoader.setCustomModelResourceLocation(boots, 0, new ModelResourceLocation("${modid}:${registryname}boots", "inventory"));</#if>
	}

	${data.getArmorModelsCode()}

}
<#-- @formatter:on -->