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

package ${package}.client.fluid;

<@javacompress>
@EventBusSubscriber(Dist.CLIENT) public class ${name}FluidExtension {

	@SubscribeEvent public static void registerFluidTypeExtensions(RegisterClientExtensionsEvent event) {
		event.registerFluidType(new IClientFluidTypeExtensions() {
			private static final ResourceLocation STILL_TEXTURE = ResourceLocation.parse("${data.textureStill.format("%s:block/%s")}");
			private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.parse("${data.textureFlowing.format("%s:block/%s")}");
			<#if data.textureRenderOverlay?has_content>
			private static final ResourceLocation RENDER_OVERLAY_TEXTURE = ResourceLocation.parse("${data.textureRenderOverlay.format("%s:textures/%s")}.png");
			</#if>

				@Override public ResourceLocation getStillTexture() {
					return STILL_TEXTURE;
				}

				@Override public ResourceLocation getFlowingTexture() {
					return FLOWING_TEXTURE;
				}

				<#if data.textureRenderOverlay?has_content>
				@Override public ResourceLocation getRenderOverlayTexture(Minecraft minecraft) {
					return RENDER_OVERLAY_TEXTURE;
				}
				</#if>

				<#if data.hasFog>
					<#if data.fogColor?has_content>
					@Override public Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
						return new Vector4f(${data.fogColor.getRed()/255}f, ${data.fogColor.getGreen()/255}f, ${data.fogColor.getBlue()/255}f, fluidFogColor.w);
					}
					</#if>

					@Override public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
						float nearDistance = fogData.environmentalStart;
						float farDistance = fogData.environmentalEnd;
						Entity entity = camera.getEntity();
						Level world = entity.level();

						fogData.environmentalStart =
							<#if hasProcedure(data.fogStartDistance)>
							(float) <@procedureOBJToNumberCode data.fogStartDistance/>
							<#else>
							${data.fogStartDistance.getFixedValue()}f
							</#if>;

						fogData.environmentalEnd =
							<#if hasProcedure(data.fogEndDistance)>
							(float) <@procedureOBJToNumberCode data.fogEndDistance/>
							<#elseif data.fogEndDistance.getFixedValue() gt 16>
							Math.min(${data.fogEndDistance.getFixedValue()}f, renderDistance)
							<#else>
							${data.fogEndDistance.getFixedValue()}f
							</#if>;
					}
				</#if>

				<#if data.isFluidTinted()>
				@Override public int getTintColor() {
					return <#if data.tintType == "Grass">
						-6506636
					<#elseif data.tintType == "Foliage" || data.tintType == "Default foliage">
						-12012264
					<#elseif data.tintType == "Birch foliage">
						-8345771
					<#elseif data.tintType == "Spruce foliage">
						-10380959
					<#elseif data.tintType == "Water">
						-13083194
					<#elseif data.tintType == "Sky">
						-8214273
					<#elseif data.tintType == "Fog">
						-4138753
					<#else>
						-16448205
					</#if>;
				}

				@Override public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
					return
					<#if data.tintType == "Grass">
						BiomeColors.getAverageGrassColor(world, pos)
					<#elseif data.tintType == "Foliage">
						BiomeColors.getAverageFoliageColor(world, pos)
					<#elseif data.tintType == "Default foliage">
						FoliageColor.FOLIAGE_DEFAULT
					<#elseif data.tintType == "Birch foliage">
						FoliageColor.FOLIAGE_BIRCH
					<#elseif data.tintType == "Spruce foliage">
						FoliageColor.FOLIAGE_EVERGREEN
					<#elseif data.tintType == "Water">
						BiomeColors.getAverageWaterColor(world, pos)
					<#elseif data.tintType == "Sky">
						Minecraft.getInstance().level.getBiome(pos).value().getSkyColor()
					<#elseif data.tintType == "Fog">
						Minecraft.getInstance().level.getBiome(pos).value().getFogColor()
					<#else>
						Minecraft.getInstance().level.getBiome(pos).value().getWaterFogColor()
					</#if> | 0xFF000000;
				}
				</#if>
		}, ${JavaModName}FluidTypes.${REGISTRYNAME}_TYPE.get());
	}

}</@javacompress>