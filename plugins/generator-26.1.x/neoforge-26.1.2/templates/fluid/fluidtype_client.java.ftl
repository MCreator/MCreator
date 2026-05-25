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

	@SubscribeEvent public static void registerRegisterFluidModels(RegisterFluidModelsEvent event) {
		event.register(new FluidModel.Unbaked(
				new Material(Identifier.parse("${data.textureStill.format("%s:block/%s")}")),
				new Material(Identifier.parse("${data.textureFlowing.format("%s:block/%s")}")),
				null,
				<#if data.isFluidTinted()>
				new FluidTintSource() {
					@Override public int color(FluidState state) {
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

					@Override public int colorInWorld(FluidState state, BlockState blockState, BlockAndTintGetter world, BlockPos pos) {
						return <#if data.tintType == "Grass">
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
							Minecraft.getInstance().gameRenderer.getMainCamera().attributeProbe().getValue(EnvironmentAttributes.SKY_COLOR, 0)
						<#elseif data.tintType == "Fog">
							Minecraft.getInstance().gameRenderer.getMainCamera().attributeProbe().getValue(EnvironmentAttributes.FOG_COLOR, 0)
						<#else>
							Minecraft.getInstance().gameRenderer.getMainCamera().attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_COLOR, 0)
						</#if> | 0xFF000000;
					}
				}
				<#else>
				null
				</#if>
		), ${JavaModName}Fluids.${REGISTRYNAME}, ${JavaModName}Fluids.FLOWING_${REGISTRYNAME});
	}

	@SubscribeEvent public static void registerFluidTypeExtensions(RegisterClientExtensionsEvent event) {
		event.registerFluidType(new IClientFluidTypeExtensions() {

			<#if data.textureRenderOverlay?has_content>
			private static final Identifier RENDER_OVERLAY_TEXTURE = Identifier.parse("${data.textureRenderOverlay.format("%s:textures/%s")}.png");
			</#if>

			<#if data.textureRenderOverlay?has_content>
			@Override public Identifier getRenderOverlayTexture(Minecraft minecraft) {
				return RENDER_OVERLAY_TEXTURE;
			}
			</#if>

			<#if data.hasFog>
			<#if data.fogColor?has_content>
			@Override public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
				fluidFogColor.set(${data.fogColor.getRed()/255}f, ${data.fogColor.getGreen()/255}f, ${data.fogColor.getBlue()/255}f, fluidFogColor.w);
			}
			</#if>

			@Override public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
				float nearDistance = fogData.environmentalStart;
				float farDistance = fogData.environmentalEnd;
				Entity entity = camera.entity();
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
		}, ${JavaModName}FluidTypes.${REGISTRYNAME}_TYPE);
	}

}</@javacompress>