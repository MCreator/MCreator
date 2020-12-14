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

package ${package}.world.structure;

@${JavaModName}Elements.ModElement.Tag public class ${name}Structure extends ${JavaModName}Elements.ModElement{

	public ${name}Structure (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		MinecraftForge.EVENT_BUS.register(this);
	}

	<#if data.structure??>
	@SubscribeEvent public void addFeatureToBiomes(BiomeLoadingEvent event) {
		<#if data.restrictionBiomes?has_content>
				boolean biomeCriteria = false;
			<#list data.restrictionBiomes as restrictionBiome>
				<#if restrictionBiome.canProperlyMap()>
					if (new ResourceLocation("${restrictionBiome}").equals(event.getName()))
						biomeCriteria = true;
				</#if>
			</#list>
				if (!biomeCriteria)
					return;
		</#if>

		Feature<NoFeatureConfig> feature = new Feature<NoFeatureConfig>(NoFeatureConfig.field_236558_a_) {
			@Override public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
				int ci = (pos.getX() >> 4) << 4;
				int ck = (pos.getZ() >> 4) << 4;

				RegistryKey<World> dimensionType = world.getWorld().getDimensionKey();
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

				if ((random.nextInt(1000000) + 1) <= ${data.spawnProbability}) {
					int count = random.nextInt(${data.maxCountPerChunk - data.minCountPerChunk + 1}) + ${data.minCountPerChunk};
					for(int a = 0; a < count; a++) {
						int i = ci + random.nextInt(16);
						int k = ck + random.nextInt(16);
						int j = world.getHeight(Heightmap.Type.<#if data.surfaceDetectionType=="First block">WORLD_SURFACE_WG<#elseif data.surfaceDetectionType=="First motion blocking block">OCEAN_FLOOR_WG</#if>, i, k);

						<#if data.spawnLocation=="Ground">
							j -= 1;
						<#elseif data.spawnLocation=="Air">
							j += random.nextInt(50) + 16;
						<#elseif data.spawnLocation=="Underground">
							j = Math.abs(random.nextInt(Math.max(1, j)) - 24);
						</#if>

						<#if data.restrictionBlocks?has_content>
							BlockState blockAt = world.getBlockState(new BlockPos(i, j, k));
							boolean blockCriteria = false;
							<#list data.restrictionBlocks as restrictionBlock>
								if (blockAt.getBlock() == ${mappedBlockToBlockStateCode(restrictionBlock)}.getBlock())
									blockCriteria = true;
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

						BlockPos spawnTo = new BlockPos(i + ${data.spawnXOffset}, j + ${data.spawnHeightOffset}, k + ${data.spawnZOffset});

						int x = spawnTo.getX();
						int y = spawnTo.getY();
						int z = spawnTo.getZ();

						<#if hasCondition(data.generateCondition)>
						if (!<@procedureOBJToConditionCode data.generateCondition/>)
							continue;
						</#if>

						Template template = world.getWorld().getStructureTemplateManager().getTemplateDefaulted(new ResourceLocation("${modid}" ,"${data.structure}"));

						if (template == null)
							return false;

						template.func_237144_a_(world, spawnTo,
							new PlacementSettings()
									.setRotation(rotation)
									.setRandom(random)
									.setMirror(mirror)
									.addProcessor(BlockIgnoreStructureProcessor.${data.ignoreBlocks})
									.setChunk(null)
									.setIgnoreEntities(false), random);

						<#if hasProcedure(data.onStructureGenerated)>
							<@procedureOBJToCode data.onStructureGenerated/>
						</#if>
					}
				}

				return true;
			}
		};

		event.getGeneration().getFeatures(GenerationStage.Decoration.
				<#if data.spawnLocation=="Ground">SURFACE_STRUCTURES
				<#elseif data.spawnLocation=="Air">RAW_GENERATION
				<#elseif data.spawnLocation=="Underground">UNDERGROUND_STRUCTURES</#if>)
			 .add(() -> feature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
					 			.withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
	}
	</#if>

}
<#-- @formatter:on -->
