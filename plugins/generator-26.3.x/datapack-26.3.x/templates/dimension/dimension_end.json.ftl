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
      "legacy_random_source": true,
      "disable_mob_generation": false,
      "default_block": "${mappedMCItemToRegistryName(data.mainFillerBlock)}",
      "default_fluid": "${mappedMCItemToRegistryName(data.fluidBlock)}",
      "spawn_target": [],
      "noise": {
        "min_y": 0,
        "height": 128
      },
      <#include "end_noise_router.json.ftl">,
      "material_rule": {
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