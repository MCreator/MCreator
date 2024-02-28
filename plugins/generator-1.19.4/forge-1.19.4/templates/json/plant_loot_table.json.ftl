<#include "../mcitems.ftl">
{
  "type": "minecraft:block"
  <#if data.dropAmount != 0>,
  "pools": [
    {
      "rolls": 1.0,
      <#if data.dropAmount == 1>
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ],
      </#if>
      "entries": [
        {
          "type": "minecraft:item",
          "name": <#if data.customDrop?? && !data.customDrop.isEmpty()>"${mappedMCItemToRegistryName(data.customDrop)}"<#else>"${modid}:${registryname}"</#if>
          <#if data.plantType == "double">,
          "conditions": [
            {
              "block": "${modid}:${registryname}",
              "condition": "minecraft:block_state_property",
              "properties": {
                "half": "lower"
              }
            }
          ]
          </#if>
          <#if data.dropAmount != 1>,
          "functions": [
            {
              "count": ${data.dropAmount},
              "function": "minecraft:set_count"
            },
            {
              "function": "minecraft:explosion_decay"
            }
          ]
          </#if>
        }
      ]
    }
  ]
  </#if>
}
