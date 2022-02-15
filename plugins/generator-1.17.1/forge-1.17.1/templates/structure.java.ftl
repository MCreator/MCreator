<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.world.features;

public class ${name}Feature extends Feature<NoneFeatureConfiguration> {

	public static final ${name}Feature FEATURE = (${name}Feature) new ${name}Feature().setRegistryName("${modid}:${registryname}");
	public static final ConfiguredFeature<?, ?> CONFIGURED_FEATURE = FEATURE.configured(FeatureConfiguration.NONE);

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

	<#if data.restrictionBlocks?has_content>
	private final List<Block> base_blocks = List.of(
		<#list data.restrictionBlocks as restrictionBlock>
			${mappedBlockToBlock(restrictionBlock)}<#if restrictionBlock?has_next>,</#if>
		</#list>
	);
	</#if>

	private StructureTemplate template = null;

	public ${name}Feature() {
		super(NoneFeatureConfiguration.CODEC);
	}

	@Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		boolean dimensionCriteria = false;
		ResourceKey<Level> dimensionType = context.level().getLevel().dimension();
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

		if(template == null)
			template = context.level().getLevel().getStructureManager()
					.getOrCreate(new ResourceLocation("${modid}" ,"${data.structure}"));

		if(template == null)
			return false;

		if ((context.random().nextInt(1000000) + 1) <= ${data.spawnProbability}) {
			boolean anyPlaced = false;
			int count = context.random().nextInt(${data.maxCountPerChunk - data.minCountPerChunk + 1}) + ${data.minCountPerChunk};
			for(int a = 0; a < count; a++) {
				int i = context.origin().getX() + context.random().nextInt(16);
				int k = context.origin().getZ() + context.random().nextInt(16);

				int j = context.level().getHeight(Heightmap.Types.<#if data.surfaceDetectionType=="First block">WORLD_SURFACE_WG<#else>OCEAN_FLOOR_WG</#if>, i, k);
				<#if data.spawnLocation=="Ground">
				j -= 1;
				<#elseif data.spawnLocation=="Air">
				j += context.random().nextInt(50) + 16;
				<#elseif data.spawnLocation=="Underground">
				j = Math.abs(context.random().nextInt(Math.max(1, j)) - 24);
				</#if>

				<#if data.restrictionBlocks?has_content>
				if (!base_blocks.contains(context.level().getBlockState(new BlockPos(i, j, k)).getBlock()))
					continue;
				</#if>

				BlockPos spawnTo = new BlockPos(i + ${data.spawnXOffset}, j + ${data.spawnHeightOffset}, k + ${data.spawnZOffset});

				<#if hasProcedure(data.generateCondition) || hasProcedure(data.onStructureGenerated)>
				WorldGenLevel world = context.level();
				int x = spawnTo.getX();
				int y = spawnTo.getY();
				int z = spawnTo.getZ();
				</#if>

				<#if hasProcedure(data.generateCondition)>
				if (!<@procedureOBJToConditionCode data.generateCondition/>)
					continue;
				</#if>

				if(template.placeInWorld(context.level(), spawnTo, spawnTo, new StructurePlaceSettings()
						.setMirror(Mirror.<#if data.randomlyRotateStructure>values()[context.random().nextInt(2)]<#else>NONE</#if>)
						.setRotation(Rotation.<#if data.randomlyRotateStructure>values()[context.random().nextInt(3)]<#else>NONE</#if>)
						.setRandom(context.random())
						.addProcessor(BlockIgnoreProcessor.${data.ignoreBlocks?replace("AIR_AND_STRUCTURE_BLOCK", "STRUCTURE_AND_AIR")})
						.setIgnoreEntities(false), context.random(), 2)) {
					<#if hasProcedure(data.onStructureGenerated)>
						<@procedureOBJToCode data.onStructureGenerated/>
					</#if>

					anyPlaced = true;
				}
			}

			return anyPlaced;
		}

		return false;
	}

}
<#-- @formatter:on -->
