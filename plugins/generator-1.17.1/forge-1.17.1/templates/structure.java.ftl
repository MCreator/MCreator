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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">
package ${package}.world.structure;

@Mod.EventBusSubscriber public class ${name}Structure extends Feature<NoneFeatureConfiguration> {

	public static void addToBiome(BiomeLoadingEvent event) {
		<#if data.restrictionBiomes?has_content>
		boolean biomeCriteria = false;
			<#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
		biomeCriteria |= (event.getName() == new ResourceLocation("${restrictionBiome}"));
				</#if>
			</#list>
		if (biomeCriteria)
		</#if>
			event.getGeneration().getFeatures(GenerationStep.Decoration.
					<#if data.spawnLocation=="Ground">SURFACE_STRUCTURES
					<#elseif data.spawnLocation=="Air">RAW_GENERATION
					<#elseif data.spawnLocation=="Underground">UNDERGROUND_STRUCTURES</#if>)
					.add(() -> new ${name}Structure(NoneFeatureConfiguration.CODEC).configured(NoneFeatureConfiguration.INSTANCE)
					.decorated(FeatureDecorator.NOPE.configured(NoneDecoratorConfiguration.INSTANCE)));
	}

	public ${name}Structure(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		ServerLevel level = (ServerLevel) context.level();
		Random random = context.random();

		int ci = (context.origin().getX() >> 4) << 4;
		int ck = (context.origin().getZ() >> 4) << 4;

		<#if data.spawnWorldTypes?has_content>
		boolean dimensionCriteria = false;
			<#list data.spawnWorldTypes as worldType>
				<#if worldType=="Surface">
				dimensionCriteria |= (level.dimension() == Level.OVERWORLD);
				<#elseif worldType=="Nether">
				dimensionCriteria |= (level.dimension() == Level.NETHER);
				<#elseif worldType=="End">
				dimensionCriteria |= (level.dimension() == Level.END);
				<#else>
				dimensionCriteria |= (level.dimension() == ResourceKey.create(Registry.DIMENSION_REGISTRY,
						new ResourceLocation("${generator.getResourceLocationForModElement(worldType.toString().replace("CUSTOM:", ""))}")));
				</#if>
			</#list>
		</#if>

		if (!dimensionCriteria)
			return false;

		if ((random.nextInt(1000000) + 1) <= ${data.spawnProbability}) {
			int count = random.nextInt(${data.maxCountPerChunk - data.minCountPerChunk + 1}) + ${data.minCountPerChunk};
			for(int a = 0; a < count; a++) {
				int i = ci + random.nextInt(16);
				int k = ck + random.nextInt(16);
				int j = level.getHeightmapPos(Heightmap.Types.
					<#if data.surfaceDetectionType=="First block">WORLD_SURFACE_WG
					<#elseif data.surfaceDetectionType=="First motion blocking block">OCEAN_FLOOR_WG</#if>,
					new BlockPos(i, 0, k)).getY();

				<#if data.spawnLocation=="Ground">
					j -= 1;
				<#elseif data.spawnLocation=="Air">
					j += random.nextInt(50) + 16;
				<#elseif data.spawnLocation=="Underground">
					j = Math.abs(random.nextInt(Math.max(1, j)) - 24);
				</#if>

				<#if data.restrictionBlocks?has_content>
					BlockState blockAt = level.getBlockState(new BlockPos(i, j, k));
					boolean blockCriteria = false;
					<#list data.restrictionBlocks as restrictionBlock>
						blockCriteria |= (blockAt == ${mappedBlockToBlock(restrictionBlock)}.defaultBlockState());
					</#list>
					if (!blockCriteria)
						continue;
				</#if>

				<#if data.randomlyRotateStructure>
					Rotation rotation = Rotation.values()[random.nextInt(3)];
					Mirror mirror = Mirror.values()[random.nextInt(2)];
				<#else>
					Rotation rotation = Rotation.NONE;
					Mirror mirror = Mirror.NONE;
				</#if>

				int x = i + ${data.spawnXOffset};
				int y = j + ${data.spawnHeightOffset};
				int z = k + ${data.spawnZOffset};

				BlockPos spawnTo = new BlockPos(x, y, z);
				<#if hasProcedure(data.generateCondition) || hasProcedure(data.onStructureGenerated)>
				Level world = level;
				</#if>

				<#if hasProcedure(data.generateCondition)>
				if (!<@procedureOBJToConditionCode data.generateCondition/>)
					continue;
				</#if>

				StructureTemplate structureTemplate = level.getStructureManager().getOrCreate(new ResourceLocation("${modid}" ,"${data.structure}"));

				if (structureTemplate == null)
					return false;

				structureTemplate.placeInWorld(level, spawnTo, spawnTo,
						new StructurePlaceSettings()
								.setRotation(rotation)
								.setRandom(random)
								.setMirror(mirror)
								.addProcessor(BlockIgnoreProcessor.
									<#if data.ignoreBlocks == "AIR_AND_STRUCTURE_BLOCK">STRUCTURE_AND_AIR
									<#else>${data.ignoreBlocks}</#if>)
								.setIgnoreEntities(false), random, 3);

				<#if hasProcedure(data.onStructureGenerated)>
					<@procedureOBJToCode data.onStructureGenerated/>
				</#if>
			}
		}

		return true;
	}

}
<#-- @formatter:on -->
