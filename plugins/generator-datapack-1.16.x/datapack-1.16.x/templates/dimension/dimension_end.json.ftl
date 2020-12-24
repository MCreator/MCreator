<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "seed": 0,
    "biome_source": {
      "type": "minecraft:multi_noise",
      "seed": 0,
      "altitude_noise": {
        "firstOctave": -7,
        "amplitudes": [
          1,
          1
        ]
      },
      "temperature_noise": {
        "firstOctave": -7,
        "amplitudes": [
          1,
          1
        ]
      },
      "humidity_noise": {
        "firstOctave": -7,
        "amplitudes": [
          1,
          1
        ]
      },
      "weirdness_noise": {
        "firstOctave": -7,
        "amplitudes": [
          1,
          1
        ]
      },
      "biomes": [
        <#list data.biomesInDimension as biome>
          {
            "biome": "${biome}",
            "parameters": {
              "altitude": 0,
              <#if data.biomesInDimension?size == 1>
              "temperature": 0,
              "humidity": 0,
              <#else>
              "temperature": ${0.3 + ((1 - biome?index / (data.biomesInDimension?size - 1)) * 2 - 1) / 5},
              "humidity": ${0.3 + ((biome?index / (data.biomesInDimension?size - 1)) * 2 - 1) / 5},
              </#if>
              "weirdness": 0,
              "offset": 0
            }
          }
          <#if biome?has_next>,</#if>
        </#list>
      ]
    },
    "settings": {
      "name": "${modid}:${registryname}",
      "bedrock_roof_position": -10,
      "bedrock_floor_position": -10,
      "sea_level": 0,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
      "default_block": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)},
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "noise": {
        "density_factor": 0,
        "density_offset": 0,
        "simplex_surface_noise": true,
        "random_density_offset": false,
        "island_noise_override": true,
        "amplified": false,
        "size_horizontal": 2,
        "size_vertical": 1,
        "height": 128,
        "sampling": {
          "xz_scale": 2,
          "y_scale": 1,
          "xz_factor": 80,
          "y_factor": 160
        },
        "bottom_slide": {
          "target": -30,
          "size": 7,
          "offset": 1
        },
        "top_slide": {
          "target": -3000,
          "size": 64,
          "offset": -46
        }
      },
      "structures": {
        "structures": {}
      }
    }
  }
}
<#-- @formatter:on -->