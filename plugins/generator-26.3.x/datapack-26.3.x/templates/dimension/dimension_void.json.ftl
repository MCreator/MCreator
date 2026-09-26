<#-- @formatter:off -->
<#include "../mcitems_json.ftl">
<#import "multi_noise.json.ftl" as ms>
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
      "default_block": "minecraft:air",
      "default_fluid": "${mappedMCItemToRegistryName(data.fluidBlock)}",
      "spawn_target": [],
      "noise": {
        "min_y": 0,
        "height": 256
      },
      <#include "void_noise_router.json.ftl">,
      "material_rule": {
         "type": "minecraft:sequence",
         "sequence": []
      }
    }
  }
}
<#-- @formatter:on -->