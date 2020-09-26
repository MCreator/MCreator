<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.world.features.trees;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name} extends ${JavaModName}Elements.ModElement{
  @ObjectHolder("${modid}:${registryname}")
	public static final CustomTree tree = null;

	public ${name} (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

  public static class CustomTree extends Tree {
		public static final TreeFeatureConfig ${name?upper_case}_CONFIG = (new TreeFeatureConfig.Builder(
				new SimpleBlockStateProvider(${mappedBlockToBlockStateCode(data.treeStem)}),
				new SimpleBlockStateProvider(${mappedBlockToBlockStateCode(data.treeBranch)}),
				new BlobFoliagePlacer(${data.foliageRadius}, ${data.foliageRadiusRandom})))
        .baseHeight(${data.minHeight})
        .heightRandA(${data.randomHeight})
        .foliageHeight(${data.foliageHeight})
        .maxWaterDepth(${data.maxWaterDepth})
        .setSapling((net.minecraftforge.common.IPlantable)Blocks.JUNGLE_SAPLING)
        .build();

		protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean p_225546_2_) {
			return Feature.NORMAL_TREE.withConfiguration(${name?upper_case}_CONFIG);
		}
	}
}
