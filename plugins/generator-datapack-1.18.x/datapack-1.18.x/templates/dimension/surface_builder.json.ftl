<#-- @formatter:off -->
<#macro default biome groundBlockWithProperties undergroundBlockWithProperties>
{
  "type": "minecraft:condition",
  "if_true": {
    "type": "minecraft:biome",
    "biome_is": [
      "${biome}"
    ]
  },
  "then_run": {
    "type": "minecraft:condition",
    "if_true": {
      "type": "minecraft:above_preliminary_surface"
    },
    "then_run": {
      "type": "minecraft:sequence",
      "sequence": [
        {
          "type": "minecraft:condition",
          "if_true": {
            "type": "minecraft:stone_depth",
            "surface_type": "floor",
            "add_surface_depth": false,
            "add_surface_secondary_depth": false,
            "offset": 0
          },
          "then_run": {
            "type": "minecraft:sequence",
            "sequence": [
              {
                "type": "minecraft:condition",
                "if_true": {
                  "type": "minecraft:water",
                  "offset": -1,
                  "surface_depth_multiplier": 0,
                  "add_stone_depth": false
                },
                "then_run": {
                  "type": "minecraft:block",
                  "result_state": ${groundBlockWithProperties}
                }
              },
              {
                "type": "minecraft:block",
                "result_state": ${undergroundBlockWithProperties}
              }
            ]
          }
        },
        {
          "type": "minecraft:condition",
          "if_true": {
            "type": "minecraft:stone_depth",
            "surface_type": "floor",
            "add_surface_depth": true,
            "add_surface_secondary_depth": false,
            "offset": 0
          },
          "then_run": {
            "type": "minecraft:block",
            "result_state": ${undergroundBlockWithProperties}
          }
        }
      ]
    }
  }
}
</#macro>
<#-- @formatter:on -->