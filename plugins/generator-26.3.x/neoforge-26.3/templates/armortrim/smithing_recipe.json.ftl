<#include "../mcitems_json.ftl">
{
  "type": "minecraft:smithing_trim",
  "addition": "#minecraft:trim_materials",
  "base": "#minecraft:trimmable_armor",
  "pattern": "${modid}:${registryname}",
  "template": "${mappedMCItemToRegistryName(data.item, true)}"
}