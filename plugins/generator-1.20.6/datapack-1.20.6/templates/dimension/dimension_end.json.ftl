<#-- @formatter:off -->
<#include "../mcitems.ftl">
<#import "multi_noise.json.ftl" as ms>
<#import "surface_builder.json.ftl" as sb>
{
  "type": "${modid}:${registryname}",
  "generator": {
    "type": "minecraft:noise",
    "biome_source": <@ms.multiNoiseSource/>,
    "settings": {
      "name": "${modid}:${registryname}",
      "sea_level": 0,
      "legacy_random_source": true,
      "disable_mob_generation": false,
      "aquifers_enabled": false,
      "ore_veins_enabled": false,
      "default_block": ${mappedMCItemToBlockStateJSON(data.mainFillerBlock)},
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "spawn_target": [],
      "noise": {
        "min_y": 0,
        "height": 128,
        "size_horizontal": 2,
        "size_vertical": 1,
        "island_noise_override": true
      },
      <#include "end_noise_router.json.ftl">,
      "surface_rule": {
         "type": "minecraft:sequence",
         "sequence": [
           <#list w.filterBrokenReferences(data.biomesInDimension) as biome>
             <#if biome.getUnmappedValue().startsWith("CUSTOM:")>
               <#assign ge = w.getWorkspace().getModElementByName(biome.getUnmappedValue().replace("CUSTOM:", "")).getGeneratableElement()/>
               <@sb.defaultAny biome ge.groundBlock ge.undergroundBlock ge.getUnderwaterBlock()/>
             <#else>
               <@sb.vanilla biome/>
             </#if>
             <#if biome?has_next>,</#if>
           </#list>
         ]
      }
    }
  }
}
<#-- @formatter:on -->