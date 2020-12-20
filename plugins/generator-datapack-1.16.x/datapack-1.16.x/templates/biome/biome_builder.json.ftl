<#include "../mcitems.ftl">
{
  "config": {
    "top_material": ${mappedMCItemToBlockStateJSON(data.groundBlock)},
    "under_material": ${mappedMCItemToBlockStateJSON(data.undergroundBlock)},
    "underwater_material": ${mappedMCItemToBlockStateJSON(data.undergroundBlock)}
  },
  "type": "minecraft:default"
}