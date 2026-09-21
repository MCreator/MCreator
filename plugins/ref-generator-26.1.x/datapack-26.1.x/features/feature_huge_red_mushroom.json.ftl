<#include "mcitems_json.ftl">
{
  "cap_provider": ${mappedBlockToBlockStateProvider(input$cap)},
  "stem_provider": ${mappedBlockToBlockStateProvider(input$stem)},
  "can_place_on": {
    "type": "minecraft:matching_block_tag",
    "tag": "minecraft:huge_red_mushroom_can_place_on"
  }
  <#if field$radius != "2">, "foliage_radius": ${field$radius}</#if>
}