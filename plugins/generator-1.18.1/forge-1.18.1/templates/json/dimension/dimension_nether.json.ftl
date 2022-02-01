<#-- @formatter:off -->
<#include "../../mcitems.ftl">
<#import "multi_noise.json.ftl" as ms>
<#import "surface_builder.json.ftl" as sb>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "seed": 0,
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "sea_level": 32,
      "legacy_random_source": true,
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
        "height": 128,
        "size_horizontal": 1,
        "size_vertical": 2,
        "sampling": {
          "xz_scale": 1,
          "y_scale": 3,
          "xz_factor": 80,
          "y_factor": 60
        },
        "bottom_slide": {
          "target": 2.5,
          "size": 4,
          "offset": -1
        },
        "top_slide": {
          "target": 0.9375,
          "size": 3,
          "offset": 0
        },
        "terrain_shaper": {
          "offset": 0,
          "factor": 0,
          "jaggedness": 0
        }
      },
      "surface_rule": {
         "type": "minecraft:sequence",
         "sequence": [
           {
             "type": "minecraft:condition",
             "if_true": {
               "type": "minecraft:vertical_gradient",
               "random_name": "minecraft:bedrock_floor",
               "true_at_and_below": {
                 "above_bottom": 0
               },
               "false_at_and_above": {
                 "above_bottom": 5
               }
             },
             "then_run": {
               "type": "minecraft:block",
               "result_state": {
                 "Name": "minecraft:bedrock"
               }
             }
           },
           {
             "type": "minecraft:condition",
             "if_true": {
               "type": "minecraft:not",
               "invert": {
                 "type": "minecraft:vertical_gradient",
                 "random_name": "minecraft:bedrock_roof",
                 "true_at_and_below": {
                   "below_top": 5
                 },
                 "false_at_and_above": {
                   "below_top": 0
                 }
               }
             },
             "then_run": {
               "type": "minecraft:block",
               "result_state": {
                 "Name": "minecraft:bedrock"
               }
             }
           },
           <#list data.biomesInDimension as biome>
             <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
               <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
               <@sb.defaultAny biome ge.groundBlock ge.undergroundBlock/>
             <#else>
               <@sb.vanilla biome/>
             </#if>,
           </#list>
           {
             "type": "minecraft:block",
             "result_state": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)}
           }
         ]
      },
      "structures": {
        "structures": {}
      }
    }
  }
}
<#-- @formatter:on -->