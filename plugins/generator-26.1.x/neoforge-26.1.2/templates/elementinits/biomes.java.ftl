<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2026, Pylo, opensource contributors
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

<#assign spawn_overworld = biomes?filter(biome -> biome.spawnBiome)>
<#assign spawn_overworld_caves = biomes?filter(biome -> biome.spawnInCaves)>
<#assign spawn_nether = biomes?filter(biome -> biome.spawnBiomeNether)>

@EventBusSubscriber public class ${JavaModName}Biomes {

	public static Identifier OVERWORLD_BIOMESOURCE_PRESET_ID = Identifier.withDefaultNamespace("overworld");
	public static Identifier NETHER_BIOMESOURCE_PRESET_ID = Identifier.withDefaultNamespace("nether");

	@SubscribeEvent public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		Registry<LevelStem> levelStemTypeRegistry = event.getServer().registryAccess().lookupOrThrow(Registries.LEVEL_STEM);
		for (LevelStem levelStem : levelStemTypeRegistry.stream().toList()) {
			Holder<DimensionType> dimensionType = levelStem.type();
			if (dimensionType.is(BuiltinDimensionTypes.NETHER) || dimensionType.is(BuiltinDimensionTypes.OVERWORLD)) {
				if(levelStem.generator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
					((${JavaModName}NoiseGeneratorSettings)(Object)noiseGenerator.generatorSettings().value()).set${modid}DimensionTypeReference(dimensionType);
				}
			}
		}
	}

	public static SurfaceRules.RuleSource adaptSurfaceRule(SurfaceRules.RuleSource currentRuleSource, Holder<DimensionType> dimensionType) {
		<#if spawn_overworld?has_content || spawn_overworld_caves?has_content>
		if (dimensionType.is(BuiltinDimensionTypes.OVERWORLD)) return injectOverworldSurfaceRules(currentRuleSource);
		</#if>

		<#if spawn_nether?has_content>
		if (dimensionType.is(BuiltinDimensionTypes.NETHER)) return injectNetherSurfaceRules(currentRuleSource);
		</#if>

		return currentRuleSource;
	}

	public static <T> Climate.ParameterList<T> adaptPresetParameterList(Identifier idArg, Climate.ParameterList<T> originalList, Function<ResourceKey<Biome>, T> lookup) {
		<#if spawn_overworld?has_content || spawn_overworld_caves?has_content>
		if (idArg.equals(OVERWORLD_BIOMESOURCE_PRESET_ID)) return ${JavaModName}Biomes.modifyOverworldParameterPoints(originalList, lookup);
		</#if>

		<#if spawn_nether?has_content>
		if (idArg.equals(NETHER_BIOMESOURCE_PRESET_ID)) return ${JavaModName}Biomes.modifyNetherParameterPoints(originalList, lookup);
		</#if>

		return originalList;
	}

	<#if spawn_overworld?has_content || spawn_overworld_caves?has_content>
	private static SurfaceRules.RuleSource injectOverworldSurfaceRules(SurfaceRules.RuleSource currentRuleSource) {
		List<SurfaceRules.RuleSource> customSurfaceRules = new ArrayList<>();

		<#list spawn_overworld_caves as biome>
		customSurfaceRules.add(anySurfaceRule(
			ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")),
			${mappedBlockToBlockStateCode(biome.groundBlock)},
			${mappedBlockToBlockStateCode(biome.undergroundBlock)},
			${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
		));
		</#list>

		<#list spawn_overworld as biome>
		customSurfaceRules.add(preliminarySurfaceRule(
			ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")),
			${mappedBlockToBlockStateCode(biome.groundBlock)},
			${mappedBlockToBlockStateCode(biome.undergroundBlock)},
			${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
		));
		</#list>

		if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
			customSurfaceRules.addAll(sequenceRuleSource.sequence());
			return SurfaceRules.sequence(customSurfaceRules.toArray(SurfaceRules.RuleSource[]::new));
		} else {
			customSurfaceRules.add(currentRuleSource);
			return SurfaceRules.sequence(customSurfaceRules.toArray(SurfaceRules.RuleSource[]::new));
		}
	}

	public static <T> Climate.ParameterList<T> modifyOverworldParameterPoints(Climate.ParameterList<T> originalList, Function<ResourceKey<Biome>, T> lookup) {
		List<Pair<Climate.ParameterPoint, T>> parameters = new ArrayList<>(originalList.values());

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
			lookup.apply(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")))
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
			lookup.apply(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")))
		));
		</#list>

		<#list spawn_overworld_caves as biome>
		parameters.add(new Pair<>(
			new Climate.ParameterPoint(
				Climate.Parameter.span(${biome.genTemperature.min}f, ${biome.genTemperature.max}f),
				Climate.Parameter.span(${biome.genHumidity.min}f, ${biome.genHumidity.max}f),
				Climate.Parameter.span(${biome.genContinentalness.min}f, ${biome.genContinentalness.max}f),
				Climate.Parameter.span(${biome.genErosion.min}f, ${biome.genErosion.max}f),
				Climate.Parameter.span(${biome.genDepth.min}f, ${biome.genDepth.max}f),
				Climate.Parameter.span(${biome.genWeirdness.min}f, ${biome.genWeirdness.max}f),
				0 <#-- offset -->
			),
			lookup.apply(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")))
		));
		</#list>

		return new Climate.ParameterList<>(parameters);
	}
	</#if>

	<#if spawn_nether?has_content>
	private static SurfaceRules.RuleSource injectNetherSurfaceRules(SurfaceRules.RuleSource currentRuleSource) {
		List<SurfaceRules.RuleSource> customSurfaceRules = new ArrayList<>();

		<#list spawn_nether as biome>
		customSurfaceRules.add(anySurfaceRule(
			ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")),
			${mappedBlockToBlockStateCode(biome.groundBlock)},
			${mappedBlockToBlockStateCode(biome.undergroundBlock)},
			${mappedBlockToBlockStateCode(biome.getUnderwaterBlock())}
		));
		</#list>

		if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
			customSurfaceRules.addAll(sequenceRuleSource.sequence());
			return SurfaceRules.sequence(customSurfaceRules.toArray(SurfaceRules.RuleSource[]::new));
		} else {
			customSurfaceRules.add(currentRuleSource);
			return SurfaceRules.sequence(customSurfaceRules.toArray(SurfaceRules.RuleSource[]::new));
		}
	}

	public static <T> Climate.ParameterList<T> modifyNetherParameterPoints(Climate.ParameterList<T> originalList, Function<ResourceKey<Biome>, T> lookup) {
		List<Pair<Climate.ParameterPoint, T>> parameters = new ArrayList<>(originalList.values());

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
			lookup.apply(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")))
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
			lookup.apply(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("${modid}", "${biome.getModElement().getRegistryName()}")))
		));
		</#list>

		return new Climate.ParameterList<>(parameters);
	}
	</#if>

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
			SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.aboveBottom(5), 0),
				SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0)),
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
			)
		);
	}
	</#if>

	public interface ${JavaModName}NoiseGeneratorSettings {
		void set${modid}DimensionTypeReference(Holder<DimensionType> dimensionType);
	}

}

<#-- @formatter:on -->