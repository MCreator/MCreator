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

package ${package}.world.biome;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name}Biome extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final CustomBiome biome = null;

	public ${name}Biome (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.biomes.add(() -> new CustomBiome());
	}

	@Override public void init(FMLCommonSetupEvent event) {
		<#if data.biomeDictionaryTypes?has_content>
			BiomeDictionary.addTypes(biome,
			<#list data.biomeDictionaryTypes as biomeDictionaryType>
				BiomeDictionary.Type.${generator.map(biomeDictionaryType, "biomedictionarytypes")}<#if biomeDictionaryType?has_next>,</#if>
			</#list>
			);
		</#if>
		<#if data.spawnBiome>
		BiomeManager.addSpawnBiome(biome);
		BiomeManager.addBiome(BiomeManager.BiomeType.${data.biomeType}, new BiomeManager.BiomeEntry(biome, ${data.biomeWeight}));
        </#if>
	}

	static class CustomBiome extends Biome {

		public CustomBiome() {
			super(new Biome.Builder().downfall(${data.rainingPossibility}f)
				.depth(${data.baseHeight}f)
				.scale(${data.heightVariation}f)
				.temperature(${data.temperature}f)
				.precipitation(Biome.RainType.<#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>RAIN<#else>SNOW</#if><#else>NONE</#if>)
				.category(Biome.Category.${data.biomeCategory})
				<#if data.waterColor?has_content>
				.waterColor(${data.waterColor.getRGB()})
				<#else>
				.waterColor(4159204)
				</#if>
				<#if data.waterFogColor?has_content>
				.waterFogColor(${data.waterFogColor.getRGB()})
				<#else>
				.waterFogColor(329011)
				</#if>
				<#if data.parent?? && data.parent.getUnmappedValue() != "No parent">
				.parent("${data.parent}")
				</#if>
				.surfaceBuilder(SurfaceBuilder.DEFAULT, new SurfaceBuilderConfig(
				${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))
			);

			setRegistryName("${registryname}");

			<#list data.defaultFeatures as defaultFeature>
			DefaultBiomeFeatures.add${generator.map(defaultFeature, "defaultfeatures")}(this);
			</#list>

			<#if data.generateLakes>
			DefaultBiomeFeatures.addLakes(this);
			</#if>

			<#if data.spawnStronghold>
			this.addStructure(Feature.STRONGHOLD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnMineshaft>
			this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
			</#if>

			<#if data.spawnPillagerOutpost>
			this.addStructure(Feature.PILLAGER_OUTPOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.villageType != "none">
			this.addStructure(Feature.VILLAGE.withConfiguration(new VillageConfig("village/${data.villageType}/town_centers", 6)));
			</#if>

			<#if data.spawnWoodlandMansion>
			this.addStructure(Feature.WOODLAND_MANSION.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnJungleTemple>
			this.addStructure(Feature.JUNGLE_TEMPLE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnDesertPyramid>
			this.addStructure(Feature.DESERT_PYRAMID.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnIgloo>
			this.addStructure(Feature.IGLOO.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnOceanMonument>
			this.addStructure(Feature.OCEAN_MONUMENT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnShipwreck>
			this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)));
			</#if>

			<#if data.oceanRuinType != "NONE">
			this.addStructure(Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.${data.oceanRuinType}, 0.3F, 0.9F)));
			</#if>

			<#if (data.flowersPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.withConfiguration(DefaultBiomeFeatures.DEFAULT_FLOWER_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(${data.flowersPerChunk}))));
			</#if>

			<#if (data.grassPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.GRASS_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.grassPerChunk}))));
			</#if>

			<#if (data.seagrassPerChunk > 0)>
			this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.withConfiguration(new SeaGrassConfig(${data.seagrassPerChunk}, 0.3D)).withPlacement(Placement.TOP_SOLID_HEIGHTMAP.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
            </#if>

			<#if (data.mushroomsPerChunk > 0)>
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.BROWN_MUSHROOM_CONFIG).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(${data.mushroomsPerChunk}))));
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.RED_MUSHROOM_CONFIG).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(${data.mushroomsPerChunk}))));
			</#if>

			<#if (data.treesPerChunk > 0)>
				<#if data.treeType == data.TREES_CUSTOM>
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, new CustomTreeFeature()
						.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(${mappedBlockToBlockStateCode(data.treeStem)}), new SimpleBlockStateProvider(${mappedBlockToBlockStateCode(data.treeBranch)}))).baseHeight(${data.minHeight}).setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING).build())
						.withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
            	<#elseif data.vanillaTreeType == "Big trees">
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.FANCY_TREE.withConfiguration(DefaultBiomeFeatures.FANCY_TREE_CONFIG).withChance(0.1F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.JUNGLE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
            	<#elseif data.vanillaTreeType == "Savanna trees">
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.ACACIA_TREE.withConfiguration(DefaultBiomeFeatures.ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.OAK_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
				<#elseif data.vanillaTreeType == "Mega pine trees">
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.withConfiguration(DefaultBiomeFeatures.MEGA_PINE_TREE_CONFIG).withChance(0.30769232F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
            	<#elseif data.vanillaTreeType == "Mega spruce trees">
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.withConfiguration(DefaultBiomeFeatures.MEGA_SPRUCE_TREE_CONFIG).withChance(0.33333334F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
            	<#elseif data.vanillaTreeType == "Birch trees">
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230129_h_).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
            	<#else>
				addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230129_h_).withChance(0.2F), Feature.FANCY_TREE.withConfiguration(DefaultBiomeFeatures.field_230131_m_).withChance(0.1F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230132_o_))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1))));
				</#if>
			</#if>

			<#if (data.bigMushroomsChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_BOOLEAN_SELECTOR.withConfiguration(new TwoFeatureChoiceConfig(Feature.HUGE_RED_MUSHROOM.withConfiguration(DefaultBiomeFeatures.BIG_RED_MUSHROOM), Feature.HUGE_BROWN_MUSHROOM.withConfiguration(DefaultBiomeFeatures.BIG_BROWN_MUSHROOM))).withPlacement(Placement.COUNT_HEIGHTMAP.configure(new FrequencyConfig(${data.bigMushroomsChunk}))));
			</#if>

			<#if (data.reedsPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.SUGAR_CANE_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.reedsPerChunk}))));
			</#if>

			<#if (data.cactiPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.CACTUS_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.cactiPerChunk}))));
			</#if>

			<#if (data.sandPathcesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.SAND.getDefaultState(), 7, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))).withPlacement(Placement.COUNT_TOP_SOLID.configure(new FrequencyConfig(${data.sandPathcesPerChunk}))));
			</#if>

			<#if (data.gravelPatchesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.GRAVEL.getDefaultState(), 6, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))).withPlacement(Placement.COUNT_TOP_SOLID.configure(new FrequencyConfig(${data.gravelPatchesPerChunk}))));
			</#if>

			<#list data.spawnEntries as spawnEntry>
				<#assign entity = generator.map(spawnEntry.entity.getUnmappedValue(), "entities", 1)!"null">
				<#if entity != "null">
					<#if !entity.toString().contains(".CustomEntity")>
						this.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new Biome.SpawnListEntry(EntityType.${entity}, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
					<#else>
						this.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new Biome.SpawnListEntry(${entity.toString().replace(".CustomEntity", "")}.entity, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
					</#if>
				</#if>
			</#list>
		}

		<#if data.grassColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getGrassColor(double posX, double posZ) {
			return ${data.grassColor.getRGB()};
		}
		}
		</#if>

		<#if data.foliageColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getFoliageColor() {
			return ${data.foliageColor.getRGB()};
		}
		</#if>

		<#if data.airColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getSkyColor() {
			return ${data.airColor.getRGB()};
		}
        </#if>

	}

    <#if data.treeType == data.TREES_CUSTOM && (data.treesPerChunk > 0)>
	static class CustomTreeFeature extends AbstractTreeFeature<BaseTreeFeatureConfig> {

		CustomTreeFeature() {
			super(BaseTreeFeatureConfig::deserialize);
		}

		@Override protected boolean place(IWorldGenerationReader worldgen, Random rand, BlockPos position, Set<BlockPos> changedBlocks, Set<BlockPos> changedBlocks2, MutableBoundingBox bbox, BaseTreeFeatureConfig conf) {
			if (!(worldgen instanceof IWorld))
				return false;

			IWorld world = (IWorld) worldgen;

			int height = rand.nextInt(5) + ${data.minHeight};
			boolean spawnTree = true;

			if (position.getY() >= 1 && position.getY() + height + 1 <= world.getHeight()) {
				for (int j = position.getY(); j <= position.getY() + 1 + height; j++) {
					int k = 1;

					if (j == position.getY())
						k = 0;

					if (j >= position.getY() + height - 1)
						k = 2;

					for (int px = position.getX() - k; px <= position.getX() + k && spawnTree; px++) {
						for (int pz = position.getZ() - k; pz <= position.getZ() + k && spawnTree; pz++) {
							if (j >= 0 && j < world.getHeight()) {
								if (!this.isReplaceable(world, new BlockPos(px, j, pz))) {
									spawnTree = false;
								}
							} else {
								spawnTree = false;
							}
						}
					}
				}
				if (!spawnTree) {
					return false;
				} else {
					Block ground = world.getBlockState(position.add(0, -1, 0)).getBlock();
					Block ground2 = world.getBlockState(position.add(0, -2, 0)).getBlock();
					if (!((ground == ${mappedBlockToBlockStateCode(data.groundBlock)}.getBlock()
							|| ground == ${mappedBlockToBlockStateCode(data.undergroundBlock)}.getBlock())
							&& (ground2 == ${mappedBlockToBlockStateCode(data.groundBlock)}.getBlock()
							|| ground2 == ${mappedBlockToBlockStateCode(data.undergroundBlock)}.getBlock())
						))
						return false;

					BlockState state = world.getBlockState(position.down());
					if (position.getY() < world.getHeight() - height - 1) {
						setTreeBlockState(changedBlocks, world, position.down(), ${mappedBlockToBlockStateCode(data.undergroundBlock)}, bbox);

						for (int genh = position.getY() - 3 + height; genh <= position.getY() + height; genh++) {
							int i4 = genh - (position.getY() + height);
							int j1 = (int) (1 - i4 * 0.5);

							for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
								for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
									int j2 = i2 - position.getZ();

									if (Math.abs(position.getX()) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
										BlockPos blockpos = new BlockPos(k1, genh, i2);
										state = world.getBlockState(blockpos);

										if (state.getBlock().isAir(state, world, blockpos)
												|| state.getMaterial().blocksMovement()
												|| state.isIn(BlockTags.LEAVES)
												|| state.getBlock() == ${mappedBlockToBlockStateCode(data.treeVines)}.getBlock()
												|| state.getBlock() == ${mappedBlockToBlockStateCode(data.treeBranch)}.getBlock()) {
											setTreeBlockState(changedBlocks, world,
													blockpos, ${mappedBlockToBlockStateCode(data.treeBranch)}, bbox);
										}
									}
								}
							}
						}



						for (int genh = 0; genh < height; genh++) {
							BlockPos genhPos = position.up(genh);
							state = world.getBlockState(genhPos);

							setTreeBlockState(changedBlocks, world, genhPos, ${mappedBlockToBlockStateCode(data.treeStem)}, bbox);

							if (state.getBlock().isAir(state, world, genhPos)
										|| state.getMaterial().blocksMovement()
										|| state.isIn(BlockTags.LEAVES)
										|| state.getBlock() == ${mappedBlockToBlockStateCode(data.treeVines)}.getBlock()
										|| state.getBlock() == ${mappedBlockToBlockStateCode(data.treeBranch)}.getBlock()){

								<#if data.spawnVines>
								if (genh > 0) {
									if (rand.nextInt(3) > 0 && world.isAirBlock(position.add(-1, genh, 0)))
										setTreeBlockState(changedBlocks, world, position.add(-1, genh, 0), ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);

									if (rand.nextInt(3) > 0 && world.isAirBlock(position.add(1, genh, 0)))
										setTreeBlockState(changedBlocks, world, position.add(1, genh, 0), ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);

									if (rand.nextInt(3) > 0 && world.isAirBlock(position.add(0, genh, -1)))
										setTreeBlockState(changedBlocks, world, position.add(0, genh, -1), ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);

									if (rand.nextInt(3) > 0 && world.isAirBlock(position.add(0, genh, 1)))
										setTreeBlockState(changedBlocks, world, position.add(0, genh, 1), ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);
								}
                                </#if>
							}
						}

						<#if data.spawnVines>
							for (int genh = position.getY() - 3 + height; genh <= position.getY() + height; genh++) {
								int k4 = (int) (1 - (genh - (position.getY() + height)) * 0.5);
								for (int genx = position.getX() - k4; genx <= position.getX() + k4; genx++) {
									for (int genz = position.getZ() - k4; genz <= position.getZ() + k4; genz++) {
										BlockPos bpos = new BlockPos(genx, genh, genz);

										state = world.getBlockState(bpos);
										if (state.isIn(BlockTags.LEAVES)
												|| state.getBlock() == ${mappedBlockToBlockStateCode(data.treeBranch)}.getBlock()) {
											BlockPos blockpos1 = bpos.south();
											BlockPos blockpos2 = bpos.west();
											BlockPos blockpos3 = bpos.east();
											BlockPos blockpos4 = bpos.north();

											if (rand.nextInt(4) == 0 && world.isAirBlock(blockpos2))
												this.addVines(world, blockpos2, changedBlocks, bbox);

											if (rand.nextInt(4) == 0 && world.isAirBlock(blockpos3))
												this.addVines(world, blockpos3, changedBlocks, bbox);

											if (rand.nextInt(4) == 0 && world.isAirBlock(blockpos4))
												this.addVines(world, blockpos4, changedBlocks, bbox);

											if (rand.nextInt(4) == 0 && world.isAirBlock(blockpos1))
												this.addVines(world, blockpos1, changedBlocks, bbox);
										}
									}
								}
							}
                        </#if>

						if (rand.nextInt(4) == 0 && height > 5) {
							for (int hlevel = 0; hlevel < 2; hlevel++) {
								for (Direction Direction : Direction.Plane.HORIZONTAL) {
									if (rand.nextInt(4 - hlevel) == 0) {
										Direction dir = Direction.getOpposite();
										setTreeBlockState(changedBlocks, world, position.add(dir.getXOffset(), height - 5 + hlevel,
														dir.getZOffset()), ${mappedBlockToBlockStateCode(data.treeFruits)}, bbox);
									}
								}
							}
						}

						return true;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		private void addVines(IWorld world, BlockPos pos, Set<BlockPos> changedBlocks, MutableBoundingBox bbox) {
			setTreeBlockState(changedBlocks, world, pos, ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);
			int i = 5;
			for (BlockPos blockpos = pos.down(); world.isAirBlock(blockpos) && i > 0; --i) {
				setTreeBlockState(changedBlocks, world, blockpos, ${mappedBlockToBlockStateCode(data.treeVines)}, bbox);
				blockpos = blockpos.down();
			}
		}

		private boolean canGrowInto(Block blockType) {
        	return blockType.getDefaultState().getMaterial() == Material.AIR ||
					blockType == ${mappedBlockToBlockStateCode(data.treeStem)}.getBlock() ||
					blockType == ${mappedBlockToBlockStateCode(data.treeBranch)}.getBlock() ||
					blockType == ${mappedBlockToBlockStateCode(data.groundBlock)}.getBlock() ||
					blockType == ${mappedBlockToBlockStateCode(data.undergroundBlock)}.getBlock();
		}

		private boolean isReplaceable(IWorld world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
        	return state.getBlock().isAir(state, world, pos) || canGrowInto(state.getBlock()) || !state.getMaterial().blocksMovement();
		}

		private void setTreeBlockState(Set<BlockPos> changedBlocks, IWorldWriter world, BlockPos pos, BlockState state, MutableBoundingBox mbb) {
			super.func_227217_a_(world, pos, state, mbb);
			changedBlocks.add(pos.toImmutable());
		}

	}
    </#if>

}
<#-- @formatter:on -->
