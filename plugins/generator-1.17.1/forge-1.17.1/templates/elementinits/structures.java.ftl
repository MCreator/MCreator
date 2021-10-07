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

<#include "../procedures.java.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber public class ${JavaModName}Features {

	private static List<Feature<?>> REGISTRY = new ArrayList<>();

	<#list structures as structure>
	public static final Feature<NoneFeatureConfiguration> ${structure.getModElement().getRegistryNameUpper()} = register(
			new ${structure.getModElement().getName()}Structure(NoneFeatureConfiguration.CODEC).setRegistryName("${structure.getModElement().getRegistryName()}"));
	</#list>

	private static Feature<NoneFeatureConfiguration> register(Feature<?> feature) {
		REGISTRY.add(feature);
		return (Feature<NoneFeatureConfiguration>) feature;
	}

	@SubscribeEvent public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new Feature[0]));
		REGISTRY.forEach(e -> Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation("${modid}:" + e.getRegistryName()),
					configuredFeature((Feature<NoneFeatureConfiguration>) e)));
	}

	@SubscribeEvent public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
		<#list structures as structure>
		${structure.getModElement().getName()}Structure.addToBiome(event);
		</#list>
	}

	public static ConfiguredFeature<?, ?> configuredFeature(Feature<NoneFeatureConfiguration> feature) {
		return feature.configured(NoneFeatureConfiguration.INSTANCE)
				.decorated(FeatureDecorator.NOPE.configured(NoneDecoratorConfiguration.INSTANCE));
	}

}

<#-- @formatter:on -->