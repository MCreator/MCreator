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

package ${package}.world.features.ores;

public class ${name}Feature extends OreFeature {

	public static final ${name}Feature FEATURE = (${name}Feature) new ${name}Feature().setRegistryName("${modid}:${registryname}");
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> CONFIGURED_FEATURE = FeatureUtils.register("${modid}:${registryname}", FEATURE,
				new OreConfiguration(${name}FeatureRuleTest.INSTANCE, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.defaultBlockState(), ${data.frequencyOnChunk}));
	public static final Holder<PlacedFeature> PLACED_FEATURE = PlacementUtils.register("${modid}:${registryname}", CONFIGURED_FEATURE, List.of(
				CountPlacement.of(${data.frequencyPerChunks}),
				HeightRangePlacement.uniform(VerticalAnchor.absolute(${data.minGenerateHeight}), VerticalAnchor.absolute(${data.maxGenerateHeight}))
	));

	public static final Set<ResourceLocation> GENERATE_BIOMES =
	<#if data.restrictionBiomes?has_content>
	Set.of(
		<#list w.filterBrokenReferences(data.restrictionBiomes) as restrictionBiome>
			new ResourceLocation("${restrictionBiome}")<#sep>,
		</#list>
	);
	<#else>
	null;
	</#if>

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

	private static class ${name}FeatureRuleTest extends RuleTest {

		static final ${name}FeatureRuleTest INSTANCE = new ${name}FeatureRuleTest();
		static final com.mojang.serialization.Codec<${name}FeatureRuleTest> codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);

		static final RuleTestType<${name}FeatureRuleTest> CUSTOM_MATCH = Registry.register(Registry.RULE_TEST,
				new ResourceLocation("${modid}:${registryname}_match"), () -> codec);

		private final List<Block> base_blocks = List.of(
			<#list data.blocksToReplace as replacementBlock>
				${mappedBlockToBlock(replacementBlock)}<#sep>,
			</#list>
		);

		public boolean test(BlockState blockAt, Random random) {
			return base_blocks.contains(blockAt.getBlock());
		}

		protected RuleTestType<?> getType() {
			return CUSTOM_MATCH;
		}

	}

}

<#-- @formatter:on -->
