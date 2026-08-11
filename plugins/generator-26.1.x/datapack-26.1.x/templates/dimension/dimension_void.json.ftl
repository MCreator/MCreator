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
      "aquifers_enabled": false,
      "ore_veins_enabled": false,
      "default_block": {
		"Name": "minecraft:air"
	  },
      "default_fluid": ${mappedMCItemToBlockStateJSON(data.fluidBlock)},
      "spawn_target": [],
      "noise": {
        "min_y": -64,
        "height": 384,
        "size_horizontal": 1,
        "size_vertical": 2
      },
      <#include "void_noise_router.json.ftl">,
      "surface_rule": {
         "type": "minecraft:sequence",
         "sequence": []
      }
    }
  }
}
<#-- @formatter:on -->