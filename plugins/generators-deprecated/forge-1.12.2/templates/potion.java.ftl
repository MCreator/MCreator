<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.potion;

@Elements${JavaModName}.ModElement.Tag public class Potion${name} extends Elements${JavaModName}.ModElement {

	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final Potion potion = null;

	<#if data.registerPotionType>
	@GameRegistry.ObjectHolder("${modid}:${registryname}")
	public static final PotionType potionType = null;
	</#if>

	public Potion${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	<#if data.registerPotionType>
	@Override public void init(FMLInitializationEvent event) {
		ForgeRegistries.POTION_TYPES.register(new PotionTypeCustom());
	}

	public static class PotionTypeCustom extends PotionType {

		public PotionTypeCustom() {
			super(new PotionEffect[]{ new PotionEffect(potion, 3600) });
			setRegistryName("${registryname}");
		}

	}
	</#if>

	public static class PotionCustom extends Potion {

		private final ResourceLocation potionIcon;

		public PotionCustom() {
			super(${data.isBad}, ${data.color.getRGB()});
			<#if data.isBenefitical>
			setBeneficial();
			</#if>
			setRegistryName("${registryname}");
			setPotionName("effect.${registryname}");
			potionIcon = new ResourceLocation("${modid}:textures/mob_effect/${registryname}.png");
		}

		@Override public boolean isInstant() {
        	return ${data.isInstant};
    	}

    	@Override public List<ItemStack> getCurativeItems() {
        	List<ItemStack> ret = new ArrayList<>();
        	<#list data.curativeItems as item>
				ret.add(${mappedMCItemToItemStackCode(item,1)});
            </#list>
        	return ret;
   	 	}

   	 	@Override public boolean shouldRenderInvText(PotionEffect effect) {
    	    return ${data.renderStatusInInventory};
    	}

    	@Override public boolean shouldRenderHUD(PotionEffect effect) {
    	    return ${data.renderStatusInHUD};
    	}

		<#if hasProcedure(data.onStarted)>
			<#if data.isInstant>
			@Override public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
				World world = entity.world;
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				<@procedureOBJToCode data.onStarted/>
			}
			<#else>
			@Override public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
				World world = entity.world;
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				<@procedureOBJToCode data.onStarted/>
			}
			</#if>
		</#if>

		<#if hasProcedure(data.onActiveTick)>
		@Override public void performEffect(EntityLivingBase entity, int amplifier) {
			World world = entity.world;
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onActiveTick/>
		}
		</#if>

    	<#if hasProcedure(data.onExpired)>
		@Override public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
    		super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
    		World world = entity.world;
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onExpired/>
		}
		</#if>

		<#if data.renderStatusInInventory>
    	@SideOnly(Side.CLIENT) @Override public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if (mc.currentScreen != null) {
				mc.getTextureManager().bindTexture(potionIcon);
				Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
			}
		}
		</#if>

		<#if data.renderStatusInHUD>
		@SideOnly(Side.CLIENT) @Override public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			mc.getTextureManager().bindTexture(potionIcon);
			Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
		}
		</#if>

		@Override public boolean isReady(int duration, int amplifier) {
    		return true;
    	}

	}

}
<#-- @formatter:on -->