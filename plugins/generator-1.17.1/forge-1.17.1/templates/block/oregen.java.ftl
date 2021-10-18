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

	private static class FeatureRegisterHandler {

		@SubscribeEvent public void registerFeature(RegistryEvent.Register<Feature<?>> event) {
			event.getRegistry().register(feature);
			Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation("${modid}:${registryname}"), configuredFeature);
		}

	}

		@SubscribeEvent public void addFeatureToBiomes(BiomeLoadingEvent event) {
			event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> configuredFeature);
		}

package ${package}.world.feature.ores;

public class ${name}Feature extends OreFeature {

	public static final Feature<OreFeatureConfig> FEATURE = new (Feature<OreFeatureConfig>) ${name}Feature().setRegistryName("${registryname}");
	public static final ConfiguredFeature<?, ?> CONFIGURED_FEATURE = FEATURE
				.withConfiguration(new OreFeatureConfig(${name}FeatureRuleTest.INSTANCE, block.defaultBlockState(), ${data.frequencyOnChunk}))
				.range(${data.maxGenerateHeight})
				.square()
				.func_242731_b(${data.frequencyPerChunks});

	public static final Set<Biome> GENERATE_BIOMES = Set.of(
			<#list w.filterBrokenReferences(data.restrictionBiomes) as restrictionBiome>
			new ResourceLocation("${restrictionBiome}")<#if restrictionBiome?has_next>,</#if>
			</#list>
	);

	public ${name}Feature() {
		super(OreFeatureConfig.CODEC);
	}

	@Override public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, OreFeatureConfig config) {
		RegistryKey<World> dimensionType = world.getLevel().getDimensionKey();
		boolean dimensionCriteria = false;

        <#list data.spawnWorldTypes as worldType>
            <#if worldType=="Surface">
        		if(dimensionType == World.OVERWORLD)
        			dimensionCriteria = true;
            <#elseif worldType=="Nether">
        		if(dimensionType == World.THE_NETHER)
        			dimensionCriteria = true;
            <#elseif worldType=="End">
        		if(dimensionType == World.THE_END)
        			dimensionCriteria = true;
            <#else>
        		if(dimensionType == RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
        				new ResourceLocation("${generator.getResourceLocationForModElement(worldType.toString().replace("CUSTOM:", ""))}")))
        			dimensionCriteria = true;
            </#if>
        </#list>

		if(!dimensionCriteria)
			return false;

        <#if hasProcedure(data.generateCondition)>
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (!<@procedureOBJToConditionCode data.generateCondition/>)
        	return false;
        </#if>

		return super.generate(world, generator, rand, pos, config);
	}

	private static class ${name}FeatureRuleTest extends RuleTest {

		static final ${name}FeatureRuleTest INSTANCE = new ${name}FeatureRuleTest();
		static final com.mojang.serialization.Codec<CustomRuleTest> codec = com.mojang.serialization.Codec.unit(() -> INSTANCE);

		static final IRuleTestType<${name}FeatureRuleTest> CUSTOM_MATCH = Registry.register(Registry.RULE_TEST,
				new ResourceLocation("${modid}:${registryname}_match"), () -> CustomRuleTest.codec);

		public boolean test(BlockState blockAt, Random random) {
			boolean blockCriteria = false;

			<#list data.blocksToReplace as replacementBlock>
			if(blockAt.getBlock() == ${mappedBlockToBlock(replacementBlock)})
				blockCriteria = true;
			</#list>

			return blockCriteria;
		}

		protected IRuleTestType<?> getType() {
			return CUSTOM_MATCH;
		}

	}

}

<#-- @formatter:on -->