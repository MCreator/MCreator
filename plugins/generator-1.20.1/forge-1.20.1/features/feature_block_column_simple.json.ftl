<#include "mcitems.ftl">
{
  "allowed_placement": {
    "type": "minecraft:matching_blocks",
    "blocks": "minecraft:air"
  },
  "direction": "up",
  "layers": [
    {
      "height": ${input$height},
      "provider": ${mappedBlockToBlockStateProvider(input$block)}
    }
  ],
  "prioritize_tip": false
}