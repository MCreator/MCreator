<#-- @formatter:off -->
<#macro multiNoiseSource>
{
    "type": "minecraft:multi_noise",
    "biomes": [
      <#list data.biomesInDimension as biome>
        {
          "biome": "${biome}",
          "parameters": {
            <#if data.biomesInDimension?size == 1>
            "temperature": 0,
            "humidity": 0,
            "continentalness": 0,
            "weirdness": 0,
            <#else>
            "temperature": ${((1 - biome?index / (data.biomesInDimension?size - 1)) * 2 - 1)},
            "humidity": ${((biome?index / (data.biomesInDimension?size - 1)) * 2 - 1)},
            "continentalness": ${(biome?index % 2 == 0)?then(0.75, -0.75)},
            "weirdness": ${(biome?index % 2 == 0)?then(-1.5, 1.5)},
            </#if>
            "erosion": 0,
            "depth": 0, <#-- 0 for surface biomes, 1 for cave biomes -->
            "offset": 0
          }
        }
        <#if biome?has_next>,</#if>
      </#list>
    ]
}
</#macro>
<#-- @formatter:on -->