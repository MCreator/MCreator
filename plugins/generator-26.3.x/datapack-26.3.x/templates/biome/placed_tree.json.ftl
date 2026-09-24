<#-- @formatter:off -->
{
  "feature": "${modid}:${registryname}_tree",
  "placement": [
    {
      "type": "minecraft:count",
      "count": ${data.treesPerChunk}
    },
    {
      "type": "minecraft:in_square"
    },
    {
      "type": "minecraft:surface_water_depth_filter",
      "max_water_depth": 0
    },
    {
      "type": "minecraft:heightmap",
      "heightmap": "OCEAN_FLOOR"
    },
    {
  	  "type": "minecraft:biome"
  	},
    {
      "type": "minecraft:block_predicate_filter",
      "predicate": {
        "type": "minecraft:would_survive",
        "state": "minecraft:oak_sapling"
      }
    }
  ]
}
<#-- @formatter:on -->