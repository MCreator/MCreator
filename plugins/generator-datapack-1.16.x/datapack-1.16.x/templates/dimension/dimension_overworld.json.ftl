<#-- @formatter:off -->
<#include "../mcitems.ftl">
<#import "multi_noise.json.ftl" as ms>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "seed": ${thelper.randomlong(registryname)},
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "bedrock_roof_position": -10,
      "bedrock_floor_position": 0,
      "sea_level": 63,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
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
          "xz_scale": 1,
          "y_scale": 1,
          "xz_factor": 80,
          "y_factor": 160
        },
        "bottom_slide": {
          "target": -30,
          "size": 0,
          "offset": 0
        },
        "top_slide": {
          "target": -10,
          "size": 3,
          "offset": 0
        }
      },
      "structures": {
        "structures": {
          "minecraft:bastion_remnant": {
            "spacing": 27,
            "separation": 4,
            "salt": 30084232
          },
          "minecraft:buried_treasure": {
            "spacing": 1,
            "separation": 0,
            "salt": 0
          },
          "minecraft:desert_pyramid": {
            "spacing": 32,
            "separation": 8,
            "salt": 14357617
          },
          "minecraft:endcity": {
            "spacing": 20,
            "separation": 11,
            "salt": 10387313
          },
          "minecraft:fortress": {
            "spacing": 27,
            "separation": 4,
            "salt": 30084232
          },
          "minecraft:igloo": {
            "spacing": 32,
            "separation": 8,
            "salt": 14357618
          },
          "minecraft:jungle_pyramid": {
            "spacing": 32,
            "separation": 8,
            "salt": 14357619
          },
          "minecraft:mansion": {
            "spacing": 80,
            "separation": 20,
            "salt": 10387319
          },
          "minecraft:mineshaft": {
            "spacing": 1,
            "separation": 0,
            "salt": 0
          },
          "minecraft:monument": {
            "spacing": 32,
            "separation": 5,
            "salt": 10387313
          },
          "minecraft:nether_fossil": {
            "spacing": 2,
            "separation": 1,
            "salt": 14357921
          },
          "minecraft:ocean_ruin": {
            "spacing": 20,
            "separation": 8,
            "salt": 14357621
          },
          "minecraft:pillager_outpost": {
            "spacing": 32,
            "separation": 8,
            "salt": 165745296
          },
          "minecraft:ruined_portal": {
            "spacing": 40,
            "separation": 15,
            "salt": 34222645
          },
          "minecraft:shipwreck": {
            "spacing": 24,
            "separation": 4,
            "salt": 165745295
          },
          "minecraft:stronghold": {
            "spacing": 1,
            "separation": 0,
            "salt": 0
          },
          "minecraft:swamp_hut": {
            "spacing": 32,
            "separation": 8,
            "salt": 14357620
          },
          "minecraft:village": {
            "spacing": 32,
            "separation": 8,
            "salt": 10387312
          }
        }
      }
    }
  }
}
<#-- @formatter:on -->