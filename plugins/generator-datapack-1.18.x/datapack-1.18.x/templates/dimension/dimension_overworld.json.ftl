<#-- @formatter:off -->
<#include "../mcitems.ftl">
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
      "sea_level": 63,
      "legacy_random_source": false,
      "disable_mob_generation": ${!data.imitateOverworldBehaviour},
      "noodle_caves_enabled": ${data.imitateOverworldBehaviour},
      "noise_caves_enabled": ${data.imitateOverworldBehaviour},
      "aquifers_enabled": ${data.imitateOverworldBehaviour},
      "deepslate_enabled": ${data.imitateOverworldBehaviour},
      "ore_veins_enabled": ${data.imitateOverworldBehaviour},
      "default_block": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)},
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "noise": {
        "min_y": 0,
        "height": 256,
        "size_horizontal": 1,
        "size_vertical": 2,
        "sampling": {
          "xz_scale": 1,
          "y_scale": 1,
          "xz_factor": 80,
          "y_factor": 160
        },
        "bottom_slide": {
          "target": 0.1171875,
          "size": 3,
          "offset": 0
        },
        "top_slide": {
          "target": -0.078125,
          "size": 2,
          "offset": 8
        },
        <#include "overworld_terrain_shaper.json.ftl">
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
           <#list data.biomesInDimension as biome>
             <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
               <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
               <@sb.default biome mappedMCItemToBlockStateJSON(ge.groundBlock) mappedMCItemToBlockStateJSON(ge.undergroundBlock)/>
             <#else>
               <#-- TODO -->
             </#if>
             <#if biome?has_next>,</#if>
           </#list>
         ]
      },
      "structures": {
        "structures": {}
      }
    }
  }
}
<#-- @formatter:on -->