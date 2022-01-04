<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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

@Mod.EventBusSubscriber public class ${name}Dimension {

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public static class Fixers {

		@SubscribeEvent public static void registerFillerBlocks(FMLCommonSetupEvent event) {
			Set<Block> replaceableBlocks = new HashSet<>();
			replaceableBlocks.add(${mappedBlockToBlock(data.mainFillerBlock)});

			<#list w.filterBrokenReferences(data.biomesInDimension) as biome>
			replaceableBlocks.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation("${biome}"))
					.getGenerationSettings().getSurfaceBuilder().get().config().getTopMaterial().getBlock());
			replaceableBlocks.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation("${biome}"))
					.getGenerationSettings().getSurfaceBuilder().get().config().getUnderMaterial().getBlock());
			</#list>

			event.enqueueWork(() -> {
				WorldCarver.CAVE.replaceableBlocks = new ImmutableSet.Builder<Block>()
						.addAll(WorldCarver.CAVE.replaceableBlocks).addAll(replaceableBlocks).build();

				WorldCarver.CANYON.replaceableBlocks = new ImmutableSet.Builder<Block>()
						.addAll(WorldCarver.CANYON.replaceableBlocks).addAll(replaceableBlocks).build();
			});
		}

		@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void registerDimensionSpecialEffects(FMLClientSetupEvent event) {
			DimensionSpecialEffects customEffect = new DimensionSpecialEffects(<#if data.imitateOverworldBehaviour>128<#else>Float.NaN</#if>,
					true, <#if data.imitateOverworldBehaviour>DimensionSpecialEffects.SkyType.NORMAL<#else>DimensionSpecialEffects.SkyType.NONE</#if>, false, false) {

				@Override public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
					<#if data.airColor?has_content>
						return new Vec3(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
					<#else>
						<#if data.imitateOverworldBehaviour>
							return color.multiply(sunHeight * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight * 0.91 + 0.09);
						<#else>
							return color;
						</#if>
					</#if>
				}

				@Override public boolean isFoggyAt(int x, int y) {
					return ${data.hasFog};
				}

			};

			event.enqueueWork(() -> DimensionSpecialEffects.EFFECTS.put(new ResourceLocation("${modid}:${registryname}"), customEffect));
		}

	}

	<#if hasProcedure(data.onPlayerLeavesDimension) || hasProcedure(data.onPlayerEntersDimension)>
	@SubscribeEvent public void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		Entity entity = event.getPlayer();
		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		<#if hasProcedure(data.onPlayerLeavesDimension)>
		if (event.getFrom() == ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerLeavesDimension/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerEntersDimension)>
		if (event.getTo() == ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerEntersDimension/>
		}
        </#if>
	}
    </#if>

}
