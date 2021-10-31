<#-- @formatter:off -->
<#macro multiNoiseSource>
{
    "type": "minecraft:multi_noise",
    "seed": 0,
    "temperature_noise": {
      "firstOctave": ${-(0.21 * data.biomesInDimension?size + 4.9)},
      "amplitudes": [
        2,
        2
      ]
    },
    "humidity_noise": {
      "firstOctave": ${-(0.21 * data.biomesInDimension?size + 4.9)},
      "amplitudes": [
        2,
        2
      ]
    },
    "altitude_noise": {
      "firstOctave": ${-(0.21 * data.biomesInDimension?size + 4.9)},
      "amplitudes": [
        2,
        2
      ]
    },
    "weirdness_noise": {
      "firstOctave": ${-(0.21 * data.biomesInDimension?size + 4.9)},
      "amplitudes": [
        2,
        2
      ]
    },
    "biomes": [
      <#list data.biomesInDimension as biome>
        {
          "biome": "${biome}",
          "parameters": {
            <#if data.biomesInDimension?size == 1>
            "temperature": 0,
            "humidity": 0,
            "altitude": 0,
            "weirdness": 0,
            <#else>
            "temperature": ${((1 - biome?index / (data.biomesInDimension?size - 1)) * 2 - 1)},
            "humidity": ${((biome?index / (data.biomesInDimension?size - 1)) * 2 - 1)},
            "altitude": ${(biome?index % 2 == 0)?then(0.75, -0.75)},
            "weirdness": ${(biome?index % 2 == 0)?then(-1.5, 1.5)},
            </#if>
            "offset": 0
          }
        }
          <#if biome?has_next>,</#if>
      </#list>
    ]
}
</#macro>
<#-- @formatter:on -->