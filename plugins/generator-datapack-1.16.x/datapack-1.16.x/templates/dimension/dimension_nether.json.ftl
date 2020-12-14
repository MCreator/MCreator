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
      "bedrock_roof_position": 0,
      "bedrock_floor_position": 0,
      "sea_level": 32,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
      "default_block": {
        "Name": "${mappedMCItemToIngameNameNoTags(data.mainFillerBlock)}"
      },
      "default_fluid": {
        "Name": "${mappedMCItemToIngameNameNoTags(data.fluidBlock)}",
        "Properties": {
          "level": "0"
        }
      },
      "noise": {
        "density_factor": 0,
        "density_offset": 0.019921875,
        "simplex_surface_noise": false,
        "random_density_offset": false,
        "island_noise_override": false,
        "amplified": false,
        "size_horizontal": 1,
        "size_vertical": 2,
        "height": 128,
        "sampling": {
          "xz_scale": 1,
          "y_scale": 3,
          "xz_factor": 80,
          "y_factor": 60
        },
        "bottom_slide": {
          "target": 320,
          "size": 4,
          "offset": -1
        },
        "top_slide": {
          "target": 120,
          "size": 3,
          "offset": 0
        }
      },
      "structures": {
        "structures": {}
      }
    }
  }
}
<#-- @formatter:on -->