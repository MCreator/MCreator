<#-- @formatter:off -->
<#include "mcitems.ftl">
{
  "format_version": "1.13.0",
  "minecraft:biome": {
    "description": {
      "identifier": "${registryname}"
    },

    "components": {
      "minecraft:climate": {
        "downfall": ${data.rainingPossibility},
        <#if (data.rainingPossibility > 0) && (data.temperature > 0.15)>
            "snow_accumulation": [ 0.0, 0.25 ],
        </#if>
        "temperature": ${data.temperature}
      },
      "minecraft:overworld_height": {
        "noise_type": "${thelper.mapToString((data.genErosion.max - data.genErosion.min) / 3.0, 0, 1.5, "lowlands", "default", "extreme")}"
      },
      "minecraft:surface_parameters": {
        "sea_floor_depth": 7,
        "sea_floor_material": ${mappedMCItemToItemObjectJSON(data.getUnderwaterBlock())?replace("\"item\":", "")},
        "top_material": ${mappedMCItemToItemObjectJSON(data.groundBlock)?replace("\"item\":", "")},
        "mid_material": ${mappedMCItemToItemObjectJSON(data.undergroundBlock)?replace("\"item\":", "")},
        "foundation_material": "minecraft:stone",
        "sea_material": "minecraft:water"
      },
      "minecraft:overworld_generation_rules": {
        <#if data.spawnBiome>
            "generate_for_climates": [
              <#if (data.temperature < 0)>
              [ "cold", 1 ]
              <#elseif (data.temperature >= 0 && data.temperature < 1)>
              [ "medium", 1 ]
              <#else>
              [ "warm", 1 ]
              </#if>
            ],
        </#if>
        "hills_transformation": [
          [ "forest", ${data.treesPerChunk} ]
        ]
      },

      "animal": {},
      "monster": {},
      "overworld": {},
      "${registryname}": {}
    }
  }
}
<#-- @formatter:on -->