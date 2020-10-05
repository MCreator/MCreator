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
        "noise_type": "${thelper.mapToString(data.heightVariation, 0, 1.5, "lowlands", "default", "extreme")}"
      },
      "minecraft:surface_parameters": {
        "sea_floor_depth": ${(-12.5 * data.baseHeight + 25)?round},
        "sea_floor_material": ${mappedMCItemToIngameItemName(data.undergroundBlock)?replace("\"item\":", "")},
        "top_material": ${mappedMCItemToIngameItemName(data.groundBlock)?replace("\"item\":", "")},
        "mid_material": ${mappedMCItemToIngameItemName(data.undergroundBlock)?replace("\"item\":", "")},
        "foundation_material": "minecraft:stone",
        "sea_material": "minecraft:water"
      },
      "minecraft:overworld_generation_rules": {
        <#if data.spawnBiome>
            "generate_for_climates": [
              <#if data.biomeType == "COOL" || data.biomeType == "ICY">
              [ "cold", ${(data.biomeWeight / 10)?round} ]
              <#elseif data.biomeType == "DESERT">
              [ "medium", ${(data.biomeWeight / 10)?round} ]
              <#else>
              [ "warm", ${(data.biomeWeight / 10)?round} ]
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