<#include "../mcitems.ftl">
{
  <#if data.plantType == "growapable">
  "type": "minecraft:block_column",
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
        "id": "${modid}:${registryname}"
      }
    }
  ],
  "prioritize_tip": false
  <#else>
  "type": "minecraft:simple_block",
  "to_place": {
    "id": "${modid}:${registryname}"
  }
  </#if>
}