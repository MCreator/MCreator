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
<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

import com.mojang.datafixers.util.Pair;
import com.google.common.base.Suppliers;

<#assign spawn_overworld = biomes?filter(biome -> biome.spawnBiome)>
<#assign spawn_overworld_caves = biomes?filter(biome -> biome.spawnInCaves)>
<#assign spawn_nether = biomes?filter(biome -> biome.spawnBiomeNether)>

@Mod.EventBusSubscriber public class ${JavaModName}Biomes {

	@SubscribeEvent public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();
		Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
		Registry<LevelStem> levelStemTypeRegistry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
		Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);

		for (LevelStem levelStem : levelStemTypeRegistry.stream().toList()) {
			DimensionType dimensionType = levelStem.type().value();

			<#if spawn_overworld?has_content || spawn_overworld_caves?has_content>
			if(dimensionType == dimensionTypeRegistry.getOrThrow(BuiltinDimensionTypes.OVERWORLD)) {
				ChunkGenerator chunkGenerator = levelStem.generator();

				// Inject biomes to biome source
				if(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource noiseSource) {
					List<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters = new ArrayList<>(noiseSource.parameters().values());

					<#list spawn_overworld as biome>
					parameters.add(new Pair<>(
						new Climate.ParameterPoint(
							Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
							Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
							Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
							Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
							Climate.Parameter.point(0.0f),
							Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
							0 <#-- offset -->
						),
						biomeRegistry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")))
					));
					parameters.add(new Pair<>(
						new Climate.ParameterPoint(
							Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
							Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
							Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
							Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
							Climate.Parameter.point(1.0f),
							Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
							0 <#-- offset -->
						),
						biomeRegistry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")))
					));
					</#list>

					<#list spawn_overworld_caves as biome>
					parameters.add(new Pair<>(
						new Climate.ParameterPoint(
							Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
							Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
							Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
							Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
							Climate.Parameter.span(0.2f, 0.9f),
							Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
							0 <#-- offset -->
						),
						biomeRegistry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")))
					));
					</#list>

					chunkGenerator.biomeSource = MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(parameters));
					chunkGenerator.featuresPerStep = Suppliers.memoize(() ->
							FeatureSorter.buildFeaturesPerStep(List.copyOf(chunkGenerator.biomeSource.possibleBiomes()), biome ->
									chunkGenerator.generationSettingsGetter.apply(biome).features(), true));
				}

				// Inject surface rules
				if(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
					NoiseGeneratorSettings noiseGeneratorSettings = noiseGenerator.settings.value();
					SurfaceRules.RuleSource currentRuleSource = noiseGeneratorSettings.surfaceRule();
					if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
						List<SurfaceRules.RuleSource> surfaceRules = new ArrayList<>(sequenceRuleSource.sequence());

						<#list spawn_overworld_caves as biome>
						surfaceRules.add(1, anySurfaceRule(
							ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")),
							${mappedBlockToBlockStateCode(biome.groundBlock)},
							${mappedBlockToBlockStateCode(biome.undergroundBlock)},
							${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
						));
						</#list>

						<#list spawn_overworld as biome>
						surfaceRules.add(1, preliminarySurfaceRule(
							ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")),
							${mappedBlockToBlockStateCode(biome.groundBlock)},
							${mappedBlockToBlockStateCode(biome.undergroundBlock)},
							${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
						));
						</#list>

						NoiseGeneratorSettings moddedNoiseGeneratorSettings = new NoiseGeneratorSettings(
							noiseGeneratorSettings.noiseSettings(),
							noiseGeneratorSettings.defaultBlock(),
							noiseGeneratorSettings.defaultFluid(),
							noiseGeneratorSettings.noiseRouter(),
							SurfaceRules.sequence(surfaceRules.toArray(SurfaceRules.RuleSource[]::new)),
							noiseGeneratorSettings.spawnTarget(),
							noiseGeneratorSettings.seaLevel(),
							noiseGeneratorSettings.disableMobGeneration(),
							noiseGeneratorSettings.aquifersEnabled(),
							noiseGeneratorSettings.oreVeinsEnabled(),
							noiseGeneratorSettings.useLegacyRandomSource()
						);
						noiseGenerator.settings = new Holder.Direct<>(moddedNoiseGeneratorSettings);
					}
				}
			}
			</#if>

			<#if spawn_nether?has_content>
			if(dimensionType == dimensionTypeRegistry.getOrThrow(BuiltinDimensionTypes.NETHER)) {
				ChunkGenerator chunkGenerator = levelStem.generator();

				// Inject biomes to biome source
				if(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource noiseSource) {
					List<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters = new ArrayList<>(noiseSource.parameters().values());

					<#list spawn_nether as biome>
					parameters.add(new Pair<>(
						new Climate.ParameterPoint(
							Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
							Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
							Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
							Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
							Climate.Parameter.point(0.0f),
							Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
							0 <#-- offset -->
						),
						biomeRegistry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")))
					));
					parameters.add(new Pair<>(
						new Climate.ParameterPoint(
							Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
							Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
							Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
							Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
							Climate.Parameter.point(1.0f),
							Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
							0 <#-- offset -->
						),
						biomeRegistry.getHolderOrThrow(ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")))
					));
					</#list>

					chunkGenerator.biomeSource = MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(parameters));
					chunkGenerator.featuresPerStep = Suppliers.memoize(() ->
							FeatureSorter.buildFeaturesPerStep(List.copyOf(chunkGenerator.biomeSource.possibleBiomes()), biome ->
									chunkGenerator.generationSettingsGetter.apply(biome).features(), true));
				}

				// Inject surface rules
				if(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
					NoiseGeneratorSettings noiseGeneratorSettings = noiseGenerator.settings.value();
					SurfaceRules.RuleSource currentRuleSource = noiseGeneratorSettings.surfaceRule();
					if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
						List<SurfaceRules.RuleSource> surfaceRules = new ArrayList<>(sequenceRuleSource.sequence());

						<#list spawn_nether as biome>
						surfaceRules.add(2, anySurfaceRule(
								ResourceKey.create(Registries.BIOME, new ResourceLocation("${modid}", "${biome.getModElement().getRegistryName()}")),
							${mappedBlockToBlockStateCode(biome.groundBlock)},
							${mappedBlockToBlockStateCode(biome.undergroundBlock)},
							${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
						));
						</#list>

						NoiseGeneratorSettings moddedNoiseGeneratorSettings = new NoiseGeneratorSettings(
								noiseGeneratorSettings.noiseSettings(),
								noiseGeneratorSettings.defaultBlock(),
								noiseGeneratorSettings.defaultFluid(),
								noiseGeneratorSettings.noiseRouter(),
								SurfaceRules.sequence(surfaceRules.toArray(SurfaceRules.RuleSource[]::new)),
								noiseGeneratorSettings.spawnTarget(),
								noiseGeneratorSettings.seaLevel(),
								noiseGeneratorSettings.disableMobGeneration(),
								noiseGeneratorSettings.aquifersEnabled(),
								noiseGeneratorSettings.oreVeinsEnabled(),
								noiseGeneratorSettings.useLegacyRandomSource()
						);
						noiseGenerator.settings = new Holder.Direct<>(moddedNoiseGeneratorSettings);
					}
				}
			}
			</#if>
		}
	}

	<#if spawn_overworld?has_content>
	private static SurfaceRules.RuleSource preliminarySurfaceRule(ResourceKey<Biome> biomeKey, BlockState groundBlock, BlockState undergroundBlock, BlockState underwaterBlock) {
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey),
			SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
						SurfaceRules.sequence(
							SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
								SurfaceRules.state(groundBlock)
							),
							SurfaceRules.state(underwaterBlock)
						)
					),
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, 0, CaveSurface.FLOOR),
						SurfaceRules.state(undergroundBlock)
					)
				)
			)
		);
	}
	</#if>

	<#if spawn_nether?has_content || spawn_overworld_caves?has_content>
	private static SurfaceRules.RuleSource anySurfaceRule(ResourceKey<Biome> biomeKey, BlockState groundBlock, BlockState undergroundBlock, BlockState underwaterBlock) {
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey),
			SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
					SurfaceRules.sequence(
						SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
							SurfaceRules.state(groundBlock)
						),
						SurfaceRules.state(underwaterBlock)
					)
				),
				SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, 0, CaveSurface.FLOOR),
					SurfaceRules.state(undergroundBlock)
				)
			)
		);
	}
	</#if>

}

<#-- @formatter:on -->