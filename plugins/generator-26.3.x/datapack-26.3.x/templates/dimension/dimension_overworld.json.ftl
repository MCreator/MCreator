<#-- @formatter:off -->
<#include "../mcitems_json.ftl">
<#import "multi_noise.json.ftl" as ms>
<#import "surface_builder.json.ftl" as sb>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "sea_level": ${data.seaLevel},
      "legacy_random_source": false,
      "disable_mob_generation": false,
      <#if data.generateAquifers>
      "aquifers": {
        "barrier": {
          "type": "minecraft:noise",
          "noise": "minecraft:aquifer_barrier",
          "xz_scale": 1,
          "y_scale": 0.5
        },
        "fluid_level_floodedness": {
          "type": "minecraft:noise",
          "noise": "minecraft:aquifer_fluid_level_floodedness",
          "xz_scale": 1,
          "y_scale": 0.67
        },
        "fluid_level_spread": {
          "type": "minecraft:noise",
          "noise": "minecraft:aquifer_fluid_level_spread",
          "xz_scale": 1,
          "y_scale": 0.7142857142857143
        },
        "lava": {
          "type": "minecraft:noise",
          "noise": "minecraft:aquifer_lava",
          "xz_scale": 1,
          "y_scale": 1
        },
        "exclusion": {
          "type": "minecraft:min",
          "left": {
            "type": "minecraft:sub",
            "left": -0.225,
            "right": "minecraft:overworld/erosion"
          },
          "right": {
            "type": "minecraft:max",
            "left": {
              "type": "minecraft:sub",
              "left": "minecraft:overworld/depth",
              "right": 0.9
            },
            "right": 0
          }
        },
        "surface_level": "minecraft:overworld/preliminary_surface_level"
      },
      </#if>
      "default_block": "${mappedMCItemToRegistryName(data.mainFillerBlock)}",
      "default_fluid": "${mappedMCItemToRegistryName(data.fluidBlock)}",
      "spawn_target": [],
      "noise": {
        "min_y": -64,
        "height": 384
      },
      <#include "overworld_noise_router.json.ftl">,
      "material_rule": {
         "type": "minecraft:sequence",
         "sequence": [
           "minecraft:bedrock_floor",
           <#if data.generateOreVeins>
           "minecraft:overworld/copper_ore_vein",
           "minecraft:overworld/iron_ore_vein",
           </#if>
           <#list w.filterBrokenReferences(data.biomesInDimension) as biome>
             <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
               <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
               <@sb.default biome ge.groundBlock ge.undergroundBlock ge.getUnderwaterBlock()/>
             <#else>
               <@sb.vanilla biome/>
             </#if>
             <#if biome?has_next>,</#if>
           </#list>
           <#list w.filterBrokenReferences(data.biomesInDimensionCaves) as biome>
		     <#if biome?is_first>,</#if>
		     <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
		       <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
		       <@sb.defaultAny biome ge.groundBlock ge.undergroundBlock ge.getUnderwaterBlock()/>
		     <#else>
		       <@sb.vanilla biome true/>
		     </#if>
		     <#if biome?has_next>,</#if>
		   </#list>
         ]
      }
    }
  }
}
<#-- @formatter:on -->