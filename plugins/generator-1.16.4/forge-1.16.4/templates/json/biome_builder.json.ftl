<#include "mcitems.ftl">
{
  "config": {
    "top_material": {
      "Name": "${mappedMCItemToIngameNameNoTags(data.groundBlock)}"
    },
    "under_material": {
      "Name": "${mappedMCItemToIngameNameNoTags(data.undergroundBlock)}"
    },
    "underwater_material": {
      "Name": "${mappedMCItemToIngameNameNoTags(data.undergroundBlock)}"
    }
  },
  "type": "minecraft:default"
}
