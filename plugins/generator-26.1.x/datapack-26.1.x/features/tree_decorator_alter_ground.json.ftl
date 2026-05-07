<#include "mcitems_json.ftl">
{
  "type": "minecraft:alter_ground",
  "provider": {
    "type": "minecraft:rule_based_state_provider",
    "rules": [
      {
        "if_true": {
          "type": "minecraft:matching_block_tag",
          "tag": "minecraft:beneath_tree_podzol_replaceable"
        },
        "then": ${mappedBlockToBlockStateProvider(input$provider)}
      }
    ]
  }
}