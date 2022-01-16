<#-- @formatter:off -->
<#include "../mcitems.ftl">
<#import "multi_noise.json.ftl" as ms>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "seed": 0,
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "sea_level": 63,
      "legacy_random_source": false,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
      "noodle_caves_enabled": ${data.imitateOverworldBehaviour},
      "noise_caves_enabled": ${data.imitateOverworldBehaviour},
      "aquifers_enabled": ${data.imitateOverworldBehaviour},
      "deepslate_enabled": ${data.imitateOverworldBehaviour},
      "ore_veins_enabled": ${data.imitateOverworldBehaviour},
      "default_block": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)},
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "noise": {
        "min_y": -64,
        "height": 384,
        "size_horizontal": 1,
        "size_vertical": 2,
        "sampling": {
          "xz_scale": 1,
          "y_scale": 1,
          "xz_factor": 80,
          "y_factor": 160
        },
        "bottom_slide": {
          "target": 0.1171875,
          "size": 3,
          "offset": 0
        },
        "top_slide": {
          "target": -0.078125,
          "size": 2,
          "offset": 8
        },
        <#include "overworld_terrain_shaper.json.ftl">
      },
       "surface_rule": {
          "type": "minecraft:sequence",
          "sequence": [
            {
              "type": "minecraft:condition",
              "if_true": {
                "type": "minecraft:vertical_gradient",
                "random_name": "minecraft:bedrock_floor",
                "true_at_and_below": {
                  "above_bottom": 0
                },
                "false_at_and_above": {
                  "above_bottom": 5
                }
              },
              "then_run": {
                "type": "minecraft:block",
                "result_state": {
                  "Name": "minecraft:bedrock"
                }
              }
            },
            {
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
                      "type": "minecraft:biome",
                      "biome_is": [
                        "minecraft:plains"
                      ]
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
                            "result_state": {
                              "Name": "minecraft:grass_block",
                              "Properties": {
                                "snowy": "false"
                              }
                            }
                          }
                        },
                        {
                          "type": "minecraft:block",
                          "result_state": {
                            "Name": "minecraft:gold_block"
                          }
                        }
                      ]
                    }
                  }
                ]
              }
            }
          ]
       },
      "structures": {
        "structures": {}
      }
    }
  }
}
<#-- @formatter:on -->