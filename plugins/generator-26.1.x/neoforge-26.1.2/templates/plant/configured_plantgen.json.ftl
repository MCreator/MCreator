<#include "../mcitems.ftl">
{
  <#if data.plantType == "growapable">
  "type": "minecraft:block_column",
  "config": {
    "allowed_placement": {
      "type": "minecraft:matching_blocks",
      "blocks": "minecraft:air"
    },
    "direction": "up",
    "layers": [
      {
        "height": {
          "type": "minecraft:biased_to_bottom",
          "min_inclusive": 2,
          "max_inclusive": 4
        },
        "provider": {
          "type": "minecraft:simple_state_provider",
          "state": {
            "Name": "${modid}:${registryname}"
          }
        }
      }
    ],
    "prioritize_tip": false
  }
  <#else>
  "type": "minecraft:simple_block",
  "config": {
    "to_place": {
      "type": "minecraft:simple_state_provider",
      "state": {
        "Name": "${modid}:${registryname}"
      }
    }
  }
  </#if>
}