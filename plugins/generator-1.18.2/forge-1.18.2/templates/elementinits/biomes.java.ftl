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
<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

import com.mojang.datafixers.util.Pair;

<#assign spawn_overworld = []>
<#assign spawn_nether = []>

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Biomes {

	public static final DeferredRegister<Biome> REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, ${JavaModName}.MODID);

    <#list biomes as biome>
    public static final RegistryObject<Biome> ${biome.getModElement().getRegistryNameUpper()}
        = REGISTRY.register("${biome.getModElement().getRegistryName()}", () -> ${biome.getModElement().getName()}Biome.createBiome());

		<#if biome.spawnBiome>
			<#assign spawn_overworld += [biome]>
		</#if>

		<#if biome.spawnBiomeNether>
			<#assign spawn_nether += [biome]>
		</#if>
    </#list>

	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
	    <#list biomes as biome>
            ${biome.getModElement().getName()}Biome.init();
        </#list>
		});
	}

	<#if spawn_overworld?has_content>
	@Mod.EventBusSubscriber public static class BiomeInjector {

		@SubscribeEvent public static void onServerAboutToStart(ServerAboutToStartEvent event) {
			MinecraftServer server = event.getServer();
			Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
			Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
			WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();

			for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : worldGenSettings.dimensions().entrySet()) {
				DimensionType dimensionType = entry.getValue().typeHolder().value();

				<#if spawn_overworld?has_content>
				if(dimensionType == dimensionTypeRegistry.getOrThrow(DimensionType.OVERWORLD_LOCATION)) {
					ChunkGenerator chunkGenerator = entry.getValue().generator();

					// Inject biomes to biome source
					if(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource noiseSource) {
						List<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters = new ArrayList<>(noiseSource.parameters.values());

						<#list spawn_overworld as biome>
						parameters.add(new Pair<>(${biome.getModElement().getName()}Biome.PARAMETER_POINT, 
							biomeRegistry.getOrCreateHolder(ResourceKey.create(Registry.BIOME_REGISTRY, ${biome.getModElement().getRegistryNameUpper()}.getId()))));
						</#list>
						
						MultiNoiseBiomeSource moddedNoiseSource = new MultiNoiseBiomeSource(new Climate.ParameterList<>(parameters), noiseSource.preset);
						chunkGenerator.biomeSource = moddedNoiseSource;
						chunkGenerator.runtimeBiomeSource = moddedNoiseSource;
					}

					// Inject surface rules
					if(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
						NoiseGeneratorSettings noiseGeneratorSettings = noiseGenerator.settings.value();
						SurfaceRules.RuleSource currentRuleSource = noiseGeneratorSettings.surfaceRule();
						if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
							List<SurfaceRules.RuleSource> surfaceRules = new ArrayList<>(sequenceRuleSource.sequence());

							<#list spawn_overworld as biome>
							surfaceRules.add(1, overworldRule(
								ResourceKey.create(Registry.BIOME_REGISTRY, ${biome.getModElement().getRegistryNameUpper()}.getId()),
								${mappedBlockToBlockStateCode(biome.groundBlock)},
								${mappedBlockToBlockStateCode(biome.undergroundBlock)}
							));
							</#list>

							NoiseGeneratorSettings moddedNoiseGeneratorSettings = new NoiseGeneratorSettings(
								noiseGeneratorSettings.noiseSettings(),
								noiseGeneratorSettings.defaultBlock(),
								noiseGeneratorSettings.defaultFluid(),
								noiseGeneratorSettings.noiseRouter(),
								SurfaceRules.sequence(surfaceRules.toArray(i -> new SurfaceRules.RuleSource[i])),
								noiseGeneratorSettings.seaLevel(),
								noiseGeneratorSettings.disableMobGeneration(),
								noiseGeneratorSettings.aquifersEnabled(),
								noiseGeneratorSettings.oreVeinsEnabled(),
								noiseGeneratorSettings.useLegacyRandomSource()
							);
							noiseGenerator.settings = new Holder.Direct(moddedNoiseGeneratorSettings);
						}
					}
				}
				</#if>
				
				<#if spawn_nether?has_content>
				if(dimensionType == dimensionTypeRegistry.getOrThrow(DimensionType.NETHER_LOCATION)) {
					ChunkGenerator chunkGenerator = entry.getValue().generator();

					// Inject biomes to biome source
					if(chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource noiseSource) {
						List<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters = new ArrayList<>(noiseSource.parameters.values());

						<#list spawn_nether as biome>
						parameters.add(new Pair<>(${biome.getModElement().getName()}Biome.PARAMETER_POINT,
								biomeRegistry.getOrCreateHolder(ResourceKey.create(Registry.BIOME_REGISTRY, ${biome.getModElement().getRegistryNameUpper()}.getId()))));
						</#list>

						MultiNoiseBiomeSource moddedNoiseSource = new MultiNoiseBiomeSource(new Climate.ParameterList<>(parameters), noiseSource.preset);
						chunkGenerator.biomeSource = moddedNoiseSource;
						chunkGenerator.runtimeBiomeSource = moddedNoiseSource;
					}

					// Inject surface rules
					if(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
						NoiseGeneratorSettings noiseGeneratorSettings = noiseGenerator.settings.value();
						SurfaceRules.RuleSource currentRuleSource = noiseGeneratorSettings.surfaceRule();
						if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
							List<SurfaceRules.RuleSource> surfaceRules = new ArrayList<>(sequenceRuleSource.sequence());

							<#list spawn_nether as biome>
							surfaceRules.add(1, netherRule(
									ResourceKey.create(Registry.BIOME_REGISTRY, ${biome.getModElement().getRegistryNameUpper()}.getId()),
								${mappedBlockToBlockStateCode(biome.groundBlock)},
								${mappedBlockToBlockStateCode(biome.undergroundBlock)}
							));
							</#list>

							NoiseGeneratorSettings moddedNoiseGeneratorSettings = new NoiseGeneratorSettings(
									noiseGeneratorSettings.noiseSettings(),
									noiseGeneratorSettings.defaultBlock(),
									noiseGeneratorSettings.defaultFluid(),
									noiseGeneratorSettings.noiseRouter(),
									SurfaceRules.sequence(surfaceRules.toArray(i -> new SurfaceRules.RuleSource[i])),
									noiseGeneratorSettings.seaLevel(),
									noiseGeneratorSettings.disableMobGeneration(),
									noiseGeneratorSettings.aquifersEnabled(),
									noiseGeneratorSettings.oreVeinsEnabled(),
									noiseGeneratorSettings.useLegacyRandomSource()
							);
							noiseGenerator.settings = new Holder.Direct(moddedNoiseGeneratorSettings);
						}
					}
				}
				</#if>
			}
		}

		<#if spawn_overworld?has_content>
		private static SurfaceRules.RuleSource overworldRule(ResourceKey<Biome> biomeKey, BlockState groundBlock, BlockState undergroundBlock) {
			return SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey),
				SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
					SurfaceRules.sequence(
						SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
							SurfaceRules.sequence(
								SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
									SurfaceRules.state(groundBlock)
								),
								SurfaceRules.state(undergroundBlock)
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
		
		<#if spawn_nether?has_content>
		private static SurfaceRules.RuleSource netherRule(ResourceKey<Biome> biomeKey, BlockState groundBlock, BlockState undergroundBlock) {
			return SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey),
				SurfaceRules.sequence(
					SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
						SurfaceRules.sequence(
							SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
								SurfaceRules.state(groundBlock)
							),
							SurfaceRules.state(undergroundBlock)
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
	</#if>

}

<#-- @formatter:on -->