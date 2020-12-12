<#include "../mcitems.ftl">
{
  "config": {
    "max_water_depth": 0,
    "ignore_vines": true,
    "heightmap": "OCEAN_FLOOR",
    "minimum_size": {
      "limit": 1,
      "lower_size": 0,
      "upper_size": 1,
      "type": "minecraft:two_layers_feature_size"
    },
    "decorators": [
    ],
    "trunk_provider": {
      "state": {
        "Properties": {
          "axis": "y"
        },
        "Name": "${mappedBlockToBlockStateCode(data.treeStem)}"
      },
      "type": "minecraft:simple_state_provider"
    },
    "leaves_provider": {
      "state": {
        "Properties": {
          "persistent": "false",
          "distance": "7"
        },
        "Name": "${mappedBlockToBlockStateCode(data.treeBranch)}"
      },
      "type": "minecraft:simple_state_provider"
    },
    "foliage_placer": {
      "radius": 2,
      "offset": 0,
      "height": 3,
      "type": "minecraft:blob_foliage_placer"
    },
    "trunk_placer": {
      "base_height": ${data.minHeight},
      "height_rand_a": 3,
      "height_rand_b": 0,
      "type": "minecraft:straight_trunk_placer"
    }
  },
  "type": "minecraft:tree"
}
