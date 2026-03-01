<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "format_version": "1.26.0",
  "minecraft:biome": {
    "description": {
      "identifier": "${modid}:${registryname}"
    },
    "components": {
      "minecraft:replace_biomes": {
        "replacements": [
          {
            "dimension": "minecraft:overworld",
            "targets": [
              "minecraft:forest"
            ],
            "amount": 0.65,
            "noise_frequency_scale": 0.4
          }
        ]
      },
      "minecraft:climate": {
        "downfall": ${data.downfall},
        "snow_accumulation": [ 0.0, 0.0 ],
        "temperature": ${data.temperature}
      },
      "minecraft:overworld_height": {
        "noise_type": "${data.noiseType}"
      },
      "minecraft:village_type": {
        "type": "${data.villageType}"
      },
      "minecraft:surface_builder": {
        "builder": {
          "type": "minecraft:overworld",
          "top_material": "${mappedMCItemToRegistryNameNoTags(data.topMaterial)}",
          "mid_material": "${mappedMCItemToRegistryNameNoTags(data.midMaterial)}",
          "foundation_material": "${mappedMCItemToRegistryNameNoTags(data.getUndergroundBlock())}",
          "sea_floor_material": "${mappedMCItemToRegistryNameNoTags(data.getUnderwaterBlock())}",
          "sea_material": "${mappedMCItemToRegistryNameNoTags(data.getOceanBlock())}",
          "sea_floor_depth": ${data.seaFloorDepth}
        }
      }
    }
  }
}
<#-- @formatter:on -->