<#-- @formatter:off -->
{
  "format_version": "1.13.0",
  "minecraft:feature_rules": {
    "description": {
      "identifier": "${modid}_${registryname}_ore_feature_rule",
      "places_feature": "${modid}:${modid}_${registryname}_ore_feature"
    },
    "conditions": {
      "placement_pass": "underground_pass",
      "minecraft:biome_filter": [
        {
          "any_of": [
            {
              "test": "has_biome_tag",
              "operator": "==",
              "value": "overworld"
            },
            {
              "test": "has_biome_tag",
              "operator": "==",
              "value": "overworld_generation"
            }
          ]
        }
      ]
    },
    "distribution": {
      "iterations": ${data.frequencyPerChunks},
      "coordinate_eval_order": "zyx",
      "x": {
        "distribution": "uniform",
        "extent": [ 0, 16 ]
      },
      "y": {
        "distribution": "uniform",
        "extent": [ ${data.minGenerateHeight}, ${data.maxGenerateHeight} ]
      },
      "z": {
        "distribution": "uniform",
        "extent": [ 0, 16 ]
      }
    }
  }
}
<#-- @formatter:on -->