<#-- @formatter:off -->
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.world;

@Elements${JavaModName}.ModElement.Tag public class World${name} extends Elements${JavaModName}.ModElement{

	public static int DIMID = ${generator.getStartIDFor("dimension") + data.getModElement().getID(1)};
	public static final boolean NETHER_TYPE = <#if data.worldGenType == "Nether like gen">true<#else>false</#if>;

	<#if data.enablePortal>
		@GameRegistry.ObjectHolder("${modid}:${registryname}_portal")
		public static final BlockCustomPortal portal = null;
	</#if>

	public static DimensionType dtype;

	public World${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	<#if data.enablePortal>
	@Override public void initElements() {
		elements.blocks.add(() -> new BlockCustomPortal());
		elements.items.add(() -> new ItemBlock(portal).setRegistryName(portal.getRegistryName()));
		elements.items.add(() -> new Item${name}().setUnlocalizedName("${registryname}").setRegistryName("${registryname}"));
	}
	</#if>

	@Override public void preInit(FMLPreInitializationEvent event) {
		if(DimensionManager.isDimensionRegistered(DIMID)) {
			DIMID = DimensionManager.getNextFreeDimId();
			System.err.println("Dimension ID for dimension ${registryname} is already registered. Falling back to ID: " + DIMID);
		}
		dtype = DimensionType.register("${registryname}","_${registryname}", DIMID, WorldProviderMod.class, true);
		DimensionManager.registerDimension(DIMID, dtype);
	}

	<#if data.enablePortal>
		@SideOnly(Side.CLIENT) @Override public void registerModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(Item${name}.block, 0, new ModelResourceLocation("${modid}:${registryname}" ,"inventory"));
		}
	</#if>

	public static class WorldProviderMod extends WorldProvider {

		@Override public void init() {
			this.biomeProvider = new BiomeProviderCustom(this.world.getSeed());
			this.nether = NETHER_TYPE;
			<#if data.hasSkyLight>
				this.hasSkyLight = true;
			</#if>
		}

		<#if !data.hasWeather>
		@Override public void calculateInitialWeather() {
		}

    	@Override public void updateWeather() {
		}

		@Override public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {
			return false;
		}

		@Override public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
			return false;
		}
        </#if>

		<#if data.dimensionMusic?? && data.dimensionMusic.toString()?has_content>
		@Override @SideOnly(Side.CLIENT) public net.minecraft.client.audio.MusicTicker.MusicType getMusicType() {
        	return EnumHelperClient.addMusicType("${data.dimensionMusic}", (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation(("${data.dimensionMusic}"))), 6000, 24000);
    	}
        </#if>

		@Override public DimensionType getDimensionType() {
			return dtype;
		}

		@Override @SideOnly(Side.CLIENT) public Vec3d getFogColor(float par1, float par2) {
			return new Vec3d(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
		}

		@Override public IChunkGenerator createChunkGenerator() {
			return new ChunkProviderModded(this.world, this.world.getSeed() - DIMID);
		}

		@Override public boolean isSurfaceWorld() {
			return ${data.imitateOverworldBehaviour};
		}

		@Override public boolean canRespawnHere() {
			return ${data.canRespawnHere};
		}

		@SideOnly(Side.CLIENT) @Override public boolean doesXZShowFog(int par1, int par2) {
			return ${data.hasFog};
		}

		@Override public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos){
        	return WorldSleepResult.${data.sleepResult};
		}

		<#if !data.isDark>
		@Override protected void generateLightBrightnessTable() {
			float f = 0.5f;
			for (int i = 0; i <= 15; ++i) {
				float f1 = 1 - (float) i / 15f;
				this.lightBrightnessTable[i] = (1 - f1) / (f1 * 3 + 1) * (1 - f) + f;
			}
		}
        </#if>

		@Override public boolean doesWaterVaporize() {
      		return ${data.doesWaterVaporize};
   		}

        <#if hasProcedure(data.onPlayerEntersDimension)>
		@Override public void onPlayerAdded(EntityPlayerMP entity) {
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onPlayerEntersDimension/>
		}
        </#if>

        <#if hasProcedure(data.onPlayerLeavesDimension)>
		@Override public void onPlayerRemoved(EntityPlayerMP entity) {
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			<@procedureOBJToCode data.onPlayerLeavesDimension/>
		}
        </#if>

	}

	<#if data.enablePortal>
		<#include "dimension/teleporter.java.ftl">
		<#include "dimension/blockportal.java.ftl">
	</#if>

	<#if data.worldGenType == "Normal world gen">
        <#include "dimension/cp_normal.java.ftl">
    <#elseif data.worldGenType == "Nether like gen">
        <#include "dimension/cp_nether.java.ftl">
    <#elseif data.worldGenType == "End like gen">
        <#include "dimension/cp_end.java.ftl">
    </#if>

	<#include "dimension/biomegen.java.ftl">

}

<#-- @formatter:on -->