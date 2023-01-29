<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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

package ${package}.world.features.ores;

public class ${name}Feature extends OreFeature {

	public static ${name}Feature FEATURE = null;
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> CONFIGURED_FEATURE = null;
	public static Holder<PlacedFeature> PLACED_FEATURE = null;
	private static final BlockState REPLACED_BLOCK = ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get().defaultBlockState();

	public static Feature<?> feature() {
		FEATURE = new ${name}Feature();
		CONFIGURED_FEATURE = FeatureUtils.register("${modid}:${registryname}", FEATURE,
			new OreConfiguration(
				List.of(
					<#list data.blocksToReplace as replacementBlock>
						OreConfiguration.target(
						    <#if replacementBlock.getUnmappedValue().startsWith("TAG:")>
							    new TagMatchTest(BlockTags.create(new ResourceLocation("${replacementBlock.getUnmappedValue().replace("TAG:", "")}"))), REPLACED_BLOCK)
						    <#else>
							    new BlockStateMatchTest(${mappedBlockToBlockStateCode(replacementBlock)}), REPLACED_BLOCK)
						    </#if><#sep>,
					</#list>
				),
				${data.frequencyOnChunk}
			)
		);
		PLACED_FEATURE = PlacementUtils.register("${modid}:${registryname}", CONFIGURED_FEATURE, List.of(
			CountPlacement.of(${data.frequencyPerChunks}),
			InSquarePlacement.spread(),
			HeightRangePlacement.${data.generationShape?lower_case}(VerticalAnchor.absolute(${data.minGenerateHeight}), VerticalAnchor.absolute(${data.maxGenerateHeight})),
			BiomeFilter.biome()
		));
		return FEATURE;
	}

	private final Set<ResourceKey<Level>> generate_dimensions = Set.of(
		<#list data.spawnWorldTypes as worldType>
			<#if worldType == "Surface">
				Level.OVERWORLD
			<#elseif worldType == "Nether">
				Level.NETHER
			<#elseif worldType == "End">
				Level.END
			<#else>
				ResourceKey.create(Registry.DIMENSION_REGISTRY,
						new ResourceLocation("${generator.getResourceLocationForModElement(worldType.toString().replace("CUSTOM:", ""))}"))
			</#if><#sep>,
		</#list>
	);

	public ${name}Feature() {
		super(OreConfiguration.CODEC);
	}

	public boolean place(FeaturePlaceContext<OreConfiguration> context) {
		WorldGenLevel world = context.level();
		if (!generate_dimensions.contains(world.getLevel().dimension()))
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
