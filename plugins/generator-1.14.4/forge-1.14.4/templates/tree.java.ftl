<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.world.features.trees;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name} extends ${JavaModName}Elements.ModElement{
	public ${name} (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	public static class CustomTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {

			CustomTree() {
				super(NoFeatureConfig::deserialize, false);
			}

			@Override public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldgen, Random rand, BlockPos position, MutableBoundingBox bbox) {
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
						BlockState state = world.getBlockState(position.down());
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

			private boolean canGrowInto(Block blockType) {
	        	return blockType.getDefaultState().getMaterial() == Material.AIR ||
						blockType == ${mappedBlockToBlockStateCode(data.treeStem)}.getBlock() ||
						blockType == ${mappedBlockToBlockStateCode(data.treeBranch)}.getBlock()
			}

			private boolean isReplaceable(IWorld world, BlockPos pos) {
				BlockState state = world.getBlockState(pos);
	        	return state.getBlock().isAir(state, world, pos) || canGrowInto(state.getBlock()) || !state.getMaterial().blocksMovement();
			}

			private void setTreeBlockState(Set<BlockPos> changedBlocks, IWorldWriter world, BlockPos pos, BlockState state, MutableBoundingBox mbb) {
				super.setLogState(changedBlocks, world, pos, state, mbb);
				changedBlocks.add(pos.toImmutable());
			}

		}
}
