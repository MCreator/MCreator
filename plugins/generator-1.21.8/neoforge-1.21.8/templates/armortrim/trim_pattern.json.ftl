<#include "../mcitems_json.ftl">
{
  "asset_id": "${modid}:${data.getModElement().getRegistryName()}",
  "description": {
    "translate": "trim_pattern.${modid}.${data.getModElement().getRegistryName()}"
  },
  "template_item": "${mappedMCItemToRegistryName(data.item, true)}"
}