<#-- @formatter:off -->
<#include "../../mcitems.ftl">
<#import "multi_noise.json.ftl" as ms>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "seed": 0,
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "bedrock_roof_position": -2147483648,
      "bedrock_floor_position": 0,
      "sea_level": 63,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
      "noodle_caves_enabled": ${data.imitateOverworldBehaviour},
      "noise_caves_enabled": ${data.imitateOverworldBehaviour},
      "aquifers_enabled": ${data.imitateOverworldBehaviour},
      "deepslate_enabled": ${data.imitateOverworldBehaviour},
      "ore_veins_enabled": ${data.imitateOverworldBehaviour},
      "min_surface_level": 0,
      "default_block": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)},
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "noise": {
        "min_y": 0,
        "height": 256,
        "density_factor": 1,
        "density_offset": -0.46875,
        "size_horizontal": 1,
        "size_vertical": 2,
        "simplex_surface_noise": true,
        "random_density_offset": true,
        "island_noise_override": false,
        "amplified": false,
        "sampling": {
          "xz_scale": 0.9999999814507745,
          "y_scale": 0.9999999814507745,
          "xz_factor": 80,
          "y_factor": 160
        },
        "bottom_slide": {
          "target": 15,
          "size": 3,
          "offset": 0
        },
        "top_slide": {
          "target": -10,
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