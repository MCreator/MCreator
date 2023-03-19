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

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber public class ${JavaModName}Features {

	public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, ${JavaModName}.MODID);

	private static final List<FeatureRegistration> FEATURE_REGISTRATIONS = new ArrayList<>();

	<#list features as feature>
		<#if feature.getModElement().getTypeString() == "block">
			public static final RegistryObject<Feature<?>> ${feature.getModElement().getRegistryNameUpper()} =
				register("${feature.getModElement().getRegistryName()}", ${feature.getModElement().getName()}Feature::feature,
						new FeatureRegistration(GenerationStep.Decoration.UNDERGROUND_ORES,
							${feature.getModElement().getName()}Feature.GENERATE_BIOMES,
							${feature.getModElement().getName()}Feature::placedFeature)
				);
		<#elseif feature.getModElement().getTypeString() == "fluid">
			public static final RegistryObject<Feature<?>> ${feature.getModElement().getRegistryNameUpper()} =
				register("${feature.getModElement().getRegistryName()}", ${feature.getModElement().getName()}Feature::feature,
						new FeatureRegistration(GenerationStep.Decoration.LAKES,
							${feature.getModElement().getName()}Feature.GENERATE_BIOMES,
							${feature.getModElement().getName()}Feature::placedFeature)
				);
		<#elseif feature.getModElement().getTypeString() == "plant">
			public static final RegistryObject<Feature<?>> ${feature.getModElement().getRegistryNameUpper()} =
				register("${feature.getModElement().getRegistryName()}", ${feature.getModElement().getName()}Feature::feature,
						new FeatureRegistration(GenerationStep.Decoration.VEGETAL_DECORATION,
							${feature.getModElement().getName()}Feature.GENERATE_BIOMES,
							${feature.getModElement().getName()}Feature::placedFeature)
				);
		<#elseif feature.getModElement().getTypeString() == "structure">
			public static final RegistryObject<Feature<?>> ${feature.getModElement().getRegistryNameUpper()} =
				register("${feature.getModElement().getRegistryName()}", ${feature.getModElement().getName()}Feature::feature,
						new FeatureRegistration(GenerationStep.Decoration.
						<#if feature.spawnLocation == "Air">RAW_GENERATION<#elseif feature.spawnLocation == "Underground">UNDERGROUND_STRUCTURES<#else>SURFACE_STRUCTURES</#if>,
							${feature.getModElement().getName()}Feature.GENERATE_BIOMES,
							${feature.getModElement().getName()}Feature::placedFeature)
				);
		<#elseif feature.getModElement().getTypeString() == "feature">
			public static final RegistryObject<Feature<?>> ${feature.getModElement().getRegistryNameUpper()} =
				register("${feature.getModElement().getRegistryName()}", ${feature.getModElement().getName()}Feature::feature,
						new FeatureRegistration(GenerationStep.Decoration.${generator.map(feature.generationStep, "generationsteps")},
							${feature.getModElement().getName()}Feature.GENERATE_BIOMES,
							${feature.getModElement().getName()}Feature::placedFeature)
				);
		</#if>
	</#list>

	private static RegistryObject<Feature<?>> register(String registryname, Supplier<Feature<?>> feature, FeatureRegistration featureRegistration) {
		FEATURE_REGISTRATIONS.add(featureRegistration);
		return REGISTRY.register(registryname, feature);
	}

	@SubscribeEvent public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
		for (FeatureRegistration registration : FEATURE_REGISTRATIONS) {
			if (registration.biomes() == null || registration.biomes().contains(event.getName()))
				event.getGeneration().getFeatures(registration.stage()).add(registration.placedFeature().get());
		}
	}

	private static record FeatureRegistration (GenerationStep.Decoration stage, Set<ResourceLocation> biomes, Supplier<Holder<PlacedFeature>> placedFeature) {}

}

<#-- @formatter:on -->