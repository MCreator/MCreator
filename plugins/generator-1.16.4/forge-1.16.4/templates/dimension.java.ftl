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

	<#if data.enablePortal>
	@ObjectHolder("${modid}:${registryname}_portal")
	public static final CustomPortalBlock portal = null;
	</#if>

	public ${name}Dimension (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		<#if hasProcedure(data.onPlayerLeavesDimension) || hasProcedure(data.onPlayerEntersDimension)>
		MinecraftForge.EVENT_BUS.register(this);
		</#if>

		<#if data.enablePortal>
		FMLJavaModLoadingContext.get().getModEventBus().register(new POIRegisterHandler());
		</#if>

		<#-- register filler block to carvers -->
		DeferredWorkQueue.runLater(() -> {
			try {
				ObfuscationReflectionHelper.setPrivateValue(WorldCarver.class, WorldCarver.CAVE, new ImmutableSet.Builder<Block>()
						.addAll((Set<Block>) ObfuscationReflectionHelper.getPrivateValue(WorldCarver.class, WorldCarver.CAVE, "field_222718_j"))
						.add(${mappedBlockToBlockStateCode(data.mainFillerBlock)}.getBlock()).build(), "field_222718_j");

				ObfuscationReflectionHelper.setPrivateValue(WorldCarver.class, WorldCarver.CANYON, new ImmutableSet.Builder<Block>()
						.addAll((Set<Block>) ObfuscationReflectionHelper.getPrivateValue(WorldCarver.class, WorldCarver.CANYON, "field_222718_j"))
						.add(${mappedBlockToBlockStateCode(data.mainFillerBlock)}.getBlock()).build(), "field_222718_j");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override @OnlyIn(Dist.CLIENT) public void clientLoad(FMLClientSetupEvent event) {
		<#-- custom dimension effect -->
		DimensionRenderInfo customEffect = new DimensionRenderInfo(<#if data.imitateOverworldBehaviour>128<#else>Float.NaN</#if>,
				true, <#if data.imitateOverworldBehaviour>DimensionRenderInfo.FogType.NORMAL<#else>DimensionRenderInfo.FogType.NONE</#if>, false, false) {

			@Override public Vector3d func_230494_a_(Vector3d color, float sunHeight) {
				<#if data.airColor?has_content>
					return new Vector3d(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
				<#else>
					<#if data.imitateOverworldBehaviour>
						return color.mul(sunHeight * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight * 0.91 + 0.09);
					<#else>
						return color;
					</#if>
				</#if>
			}

			@Override public boolean func_230493_a_(int x, int y) {
				return ${data.hasFog};
			}

		};

		DeferredWorkQueue.runLater(() -> {
			try {
				Object2ObjectMap<ResourceLocation, DimensionRenderInfo> effectsRegistry =
						(Object2ObjectMap<ResourceLocation, DimensionRenderInfo>) ObfuscationReflectionHelper.getPrivateValue(DimensionRenderInfo.class, null, "field_239208_a_");
				effectsRegistry.put(new ResourceLocation("${modid}:${registryname}"), customEffect);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		<#if data.enablePortal>
		RenderTypeLookup.setRenderLayer(portal, RenderType.getTranslucent());
		</#if>
	}

	<#if data.enablePortal>
		private static PointOfInterestType poi = null;
		public static final TicketType<BlockPos> CUSTOM_PORTAL = TicketType.create("${registryname}_portal", Vector3i::compareTo, 300);

		public static class POIRegisterHandler {
			@SubscribeEvent public void registerPointOfInterest(RegistryEvent.Register<PointOfInterestType> event) {
				poi = new PointOfInterestType("${registryname}_portal",
						Sets.newHashSet(ImmutableSet.copyOf(portal.getStateContainer().getValidStates())), 0, 1).setRegistryName("${registryname}_portal");
				ForgeRegistries.POI_TYPES.register(poi);
			}
		}

		@Override public void initElements() {
			elements.blocks.add(() -> new CustomPortalBlock());
			elements.items.add(() -> new ${name}Item().setRegistryName("${registryname}"));
		}

		<#include "dimension/blockportal.java.ftl">
		<#include "dimension/teleporter.java.ftl">
	</#if>

	<#if hasProcedure(data.onPlayerLeavesDimension) || hasProcedure(data.onPlayerEntersDimension)>
	@SubscribeEvent public void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		Entity entity = event.getPlayer();
		World world = entity.world;
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();

		<#if hasProcedure(data.onPlayerLeavesDimension)>
		if (event.getFrom() == RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerLeavesDimension/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerEntersDimension)>
		if (event.getTo() == RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerEntersDimension/>
		}
        </#if>
	}
	</#if>

}

<#-- @formatter:on -->