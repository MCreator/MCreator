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

package ${package}.world.dimension;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name}Dimension extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final ModDimension dimension = null;

	<#if data.enablePortal>
	@ObjectHolder("${modid}:${registryname}_portal")
	public static final CustomPortalBlock portal = null;
	</#if>

	public static DimensionType type = null;

	private static Biome[] dimensionBiomes;

	public ${name}Dimension (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent public void registerDimension(RegistryEvent.Register<ModDimension> event) {
		event.getRegistry().register(new CustomModDimension().setRegistryName("${registryname}"));
	}

	@SubscribeEvent public void onRegisterDimensionsEvent(RegisterDimensionsEvent event) {
		if (DimensionType.byName(new ResourceLocation("${modid}:${registryname}")) == null) {
			DimensionManager.registerDimension(new ResourceLocation("${modid}:${registryname}"), dimension, null, ${data.hasSkyLight});
		}

		type = DimensionType.byName(new ResourceLocation("${modid}:${registryname}"));
	}

	@Override public void init(FMLCommonSetupEvent event) {
		dimensionBiomes = new Biome[] {
    		<#list data.biomesInDimension as biome>
				<#if biome.canProperlyMap()>
				ForgeRegistries.BIOMES.getValue(new ResourceLocation("${biome}")),
				</#if>
			</#list>
		};
	}

	<#if data.enablePortal>
		@Override public void initElements() {
			elements.blocks.add(() -> new CustomPortalBlock());
			elements.items.add(() -> new ${name}Item().setRegistryName("${registryname}"));
		}

		@Override @OnlyIn(Dist.CLIENT) public void clientLoad(FMLClientSetupEvent event) {
			RenderTypeLookup.setRenderLayer(portal, RenderType.getTranslucent());
		}

		<#include "dimension/blockportal.java.ftl">
		<#include "dimension/teleporter.java.ftl">
	</#if>

	public static class CustomModDimension extends ModDimension {

		@Override
		public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
			return CustomDimension::new;
		}

	}

	public static class CustomDimension extends Dimension {

		private BiomeProviderCustom biomeProviderCustom = null;

		public CustomDimension(World world, DimensionType type) {
			super(world, type, <#if data.isDark>0<#else>0.5f</#if>);
			this.nether = <#if data.worldGenType == "Nether like gen">true<#else>false</#if>;
		}

		<#if !data.hasWeather>
		@Override public void calculateInitialWeather() {
		}

    	@Override public void updateWeather(Runnable defaultWeather) {
		}

		@Override public boolean canDoLightning(Chunk chunk) {
			return false;
		}

		@Override public boolean canDoRainSnowIce(Chunk chunk) {
			return false;
		}
        </#if>

		@Override @OnlyIn(Dist.CLIENT) public Vec3d getFogColor(float cangle, float ticks) {
			return new Vec3d(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
		}

		@Override public ChunkGenerator<?> createChunkGenerator() {
			if(this.biomeProviderCustom == null) {
				this.biomeProviderCustom = new BiomeProviderCustom(this.world);
			}
			return new ChunkProviderModded(this.world, this.biomeProviderCustom);
		}

		@Override public boolean isSurfaceWorld() {
			return ${data.imitateOverworldBehaviour};
		}

		@Override public boolean canRespawnHere() {
			return ${data.canRespawnHere};
		}

		@OnlyIn(Dist.CLIENT) @Override public boolean doesXZShowFog(int x, int z) {
			return ${data.hasFog};
		}

		@Override public SleepResult canSleepAt(PlayerEntity player, BlockPos pos){
        	return SleepResult.${data.sleepResult};
		}

		@Nullable public BlockPos findSpawn(ChunkPos chunkPos, boolean checkValid) {
   		   return null;
   		}

   		@Nullable public BlockPos findSpawn(int x, int z, boolean checkValid) {
   		   return null;
   		}

		@Override public boolean doesWaterVaporize() {
      		return ${data.doesWaterVaporize};
   		}

		@Override ${mcc.getMethod("net.minecraft.world.dimension.OverworldDimension", "calculateCelestialAngle", "long", "float")}

	}

	<#if hasProcedure(data.onPlayerLeavesDimension) || hasProcedure(data.onPlayerEntersDimension)>
	@SubscribeEvent public void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		Entity entity = event.getPlayer();
		World world = entity.world;
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();

		<#if hasProcedure(data.onPlayerLeavesDimension)>
		if (event.getFrom() == type) {
			<@procedureOBJToCode data.onPlayerLeavesDimension/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerEntersDimension)>
		if (event.getTo() == type) {
			<@procedureOBJToCode data.onPlayerEntersDimension/>
		}
        </#if>
	}
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