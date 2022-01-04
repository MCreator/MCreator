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
<#include "../mcitems.ftl">

package ${package}.world.features.plants;

import com.mojang.serialization.Codec;

public class ${name}Feature extends <#if data.plantType == "normal" && data.staticPlantGenerationType == "Flower">DefaultFlowerFeature<#else>RandomPatchFeature</#if> {
	public static final ${name}Feature FEATURE = (${name}Feature) new ${name}Feature().setRegistryName("${modid}:${registryname}");
	public static final ConfiguredFeature<?, ?> CONFIGURED_FEATURE = FEATURE
				.configured(
					new RandomPatchConfiguration.GrassConfigurationBuilder(
						new SimpleStateProvider(${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.defaultBlockState()),
						<#if data.plantType == "double">DoublePlantPlacer.INSTANCE
						<#elseif data.plantType == "normal">SimpleBlockPlacer.INSTANCE
						<#else>new ColumnPlacer(BiasedToBottomInt.of(2, 4))
						</#if>
					)
					.tries(${data.patchSize})
					<#if data.plantType == "growapable">.xspread(4).yspread(0).zspread(4).noProjection()</#if>
					<#if data.plantType == "double" && data.doublePlantGenerationType == "Flower">.noProjection()</#if>
					.build()
				)
				.decorated(FeatureDecorator.HEIGHTMAP<#if (data.plantType == "normal" && data.staticPlantGenerationType == "Grass") || data.plantType == "growapable">_SPREAD_DOUBLE</#if>.configured(new HeightmapConfiguration(Heightmap.Types.MOTION_BLOCKING)))
				.squared()
				<#if !(data.plantType == "growapable")>.rarity(32)</#if>
				.count(${data.frequencyOnChunks});

	public static final Set<ResourceLocation> GENERATE_BIOMES =
	<#if data.restrictionBiomes?has_content>
	Set.of(
		<#list w.filterBrokenReferences(data.restrictionBiomes) as restrictionBiome>
		new ResourceLocation("${restrictionBiome}")<#if restrictionBiome?has_next>,</#if>
		</#list>
	);
	<#else>
	null;
	</#if>

	public ${name}Feature() {
		super(RandomPatchConfiguration.CODEC);
	}

	public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
		WorldGenLevel world = context.level();
		ResourceKey<Level> dimensionType = world.getLevel().dimension();
		boolean dimensionCriteria = false;

		<#list data.spawnWorldTypes as worldType>
			<#if worldType=="Surface">
				if(dimensionType == Level.OVERWORLD)
					dimensionCriteria = true;
			<#elseif worldType=="Nether">
				if(dimensionType == Level.NETHER)
					dimensionCriteria = true;
			<#elseif worldType=="End">
				if(dimensionType == Level.END)
					dimensionCriteria = true;
			<#else>
				if(dimensionType == ResourceKey.create(Registry.DIMENSION_REGISTRY,
						new ResourceLocation("${generator.getResourceLocationForModElement(worldType.toString().replace("CUSTOM:", ""))}")))
					dimensionCriteria = true;
			</#if>
		</#list>

		if(!dimensionCriteria)
			return false;

		<#if hasProcedure(data.generateCondition)>
		int x = context.origin().getX();
		int y = context.origin().getY();
		int z = context.origin().getZ();
		if (!<@procedureOBJToConditionCode data.generateCondition/>)
			return false;
		</#if>

		return super.place(context);
	}
}

<#-- @formatter:on -->