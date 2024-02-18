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
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

package ${package}.world.features;

<#compress>
public class ${name}Feature extends Feature<NoneFeatureConfiguration> {

	<#if data.restrictionBlocks?has_content>
	private final List<Block> base_blocks;
	</#if>

	private StructureTemplate template = null;

	public ${name}Feature() {
		super(NoneFeatureConfiguration.CODEC);

		<#if data.restrictionBlocks?has_content>
			base_blocks = List.of(
				<#list data.restrictionBlocks as restrictionBlock>
					${mappedBlockToBlock(restrictionBlock)}<#sep>,
				</#list>
			);
		</#if>
	}

	@Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		if (template == null)
			template = context.level().getLevel().getStructureManager()
					.getOrCreate(new ResourceLocation("${modid}", "${data.structure}"));

		if (template == null)
			return false;

		boolean anyPlaced = false;
		if ((context.random().nextInt(1000000) + 1) <= ${data.spawnProbability}) {
			int count = context.random().nextInt(${data.maxCountPerChunk - data.minCountPerChunk + 1}) + ${data.minCountPerChunk};
			for (int a = 0; a < count; a++) {
				int i = context.origin().getX() + context.random().nextInt(16);
				int k = context.origin().getZ() + context.random().nextInt(16);

				int j = context.level().getHeight(
						Heightmap.Types.<#if data.surfaceDetectionType == "First block">WORLD_SURFACE_WG<#else>OCEAN_FLOOR_WG</#if>, i, k
					)<#if data.spawnLocation == "Ground"> - 1</#if>;

				<#if data.spawnLocation == "Air">
					j += context.random().nextInt(64) + 16;
				<#elseif data.spawnLocation == "Underground">
					j = Mth.nextInt(context.random(), 8 + context.level().getMinBuildHeight(), Math.max(j, 9 + context.level().getMinBuildHeight()));
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

				if (template.placeInWorld(context.level(), spawnTo, spawnTo, new StructurePlaceSettings()
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
		}

		return anyPlaced;
	}

}
</#compress>
<#-- @formatter:on -->
