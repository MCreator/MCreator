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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Features {

	private static final Map<Feature<?>, FeatureRegistration> REGISTRY = new HashMap<>();

	static {
    <#list features as feature>
		<#if feature.getModElement().getTypeString() == "block">
			REGISTRY.put(${feature.getModElement().getName()}Feature.FEATURE, new FeatureRegistration(GenerationStep.Decoration.UNDERGROUND_ORES,
						${feature.getModElement().getName()}Feature.GENERATE_BIOMES, ${feature.getModElement().getName()}Feature.CONFIGURED_FEATURE));
		<#elseif feature.getModElement().getTypeString() == "plant">
			REGISTRY.put(${feature.getModElement().getName()}Feature.FEATURE, new FeatureRegistration(GenerationStep.Decoration.VEGETAL_DECORATION,
        				${feature.getModElement().getName()}Feature.GENERATE_BIOMES, ${feature.getModElement().getName()}Feature.CONFIGURED_FEATURE));
		<#elseif feature.getModElement().getTypeString() == "fluid">
			REGISTRY.put(${feature.getModElement().getName()}Feature.FEATURE, new FeatureRegistration(GenerationStep.Decoration.LAKES,
        				${feature.getModElement().getName()}Feature.GENERATE_BIOMES, ${feature.getModElement().getName()}Feature.CONFIGURED_FEATURE));
		<#elseif feature.getModElement().getTypeString() == "structure">
			REGISTRY.put(${feature.getModElement().getName()}Feature.FEATURE, new FeatureRegistration(GenerationStep.Decoration.
						<#if feature.spawnLocation=="Air">RAW_GENERATION<#elseif feature.spawnLocation=="Underground">UNDERGROUND_STRUCTURES<#else>SURFACE_STRUCTURES</#if>,
						${feature.getModElement().getName()}Feature.GENERATE_BIOMES, ${feature.getModElement().getName()}Feature.CONFIGURED_FEATURE));
		</#if>
    </#list>
	}

	@SubscribeEvent public static void registerFeature(RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().registerAll(REGISTRY.keySet().toArray(new Feature[0]));

		REGISTRY.forEach((feature, registration) -> Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, feature.getRegistryName(), registration.configuredFeature()));
	}

	@Mod.EventBusSubscriber private static class BiomeFeatureLoader {

		@SubscribeEvent public static void addFeatureToBiomes(BiomeLoadingEvent event) {
			for (FeatureRegistration registration : REGISTRY.values()) {
				if (registration.biomes() == null || registration.biomes().contains(event.getName())) {
					event.getGeneration().getFeatures(registration.stage()).add(() -> registration.configuredFeature());
				}
			}
		}

	}

	private static record FeatureRegistration (GenerationStep.Decoration stage, Set<ResourceLocation> biomes, ConfiguredFeature<?, ?> configuredFeature) {}

}

<#-- @formatter:on -->