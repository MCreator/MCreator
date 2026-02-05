<#include "../mcitems.ftl">
<#assign isFlowerPot = data.getModElement().getTypeString() == "block" && data.blockBase! == "FlowerPot">
<#assign isSlab = data.getModElement().getTypeString() == "block" && data.blockBase! == "Slab">
{
  "type": "minecraft:block",
  "random_sequence": "${modid}:blocks/${registryname}"
  <#if data.hasDrops() || isFlowerPot>,
  "pools": [
    <#if isFlowerPot>
    {
      "rolls": 1.0,
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ],
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:flower_pot"
        }
      ]
    }<#if data.hasDrops()>,</#if>
    </#if>
    <#if data.hasDrops()>
    {
      "rolls": 1.0,
      <#if data.dropAmount == 1 && !isSlab>
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ],
      </#if>
      "entries": [
        {
          "type": "minecraft:item",
          "name": <#if data.customDrop?? && !data.customDrop.isEmpty()>"${mappedMCItemToRegistryName(data.customDrop)}"
            <#elseif isFlowerPot>"${mappedMCItemToRegistryName(data.pottedPlant)}"
            <#else>"${modid}:${registryname}"</#if>
          <#if data.isDoubleBlock()>,
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
          <#if data.dropAmount != 1 || isSlab>,
          "functions": [
            <#if data.dropAmount != 1>
            {
              "count": ${data.dropAmount},
              "function": "minecraft:set_count"
            },
            </#if>
            <#if isSlab> <#-- Drop twice the amount if it's a double slab -->
            {
              "function": "minecraft:set_count",
              "count": ${data.dropAmount * 2},
              "conditions": [
                {
                  "condition": "minecraft:block_state_property",
                  "block": "${modid}:${registryname}",
                  "properties": {
                    "type": "double"
                  }
                }
              ]
            },
            </#if>
            {
              "function": "minecraft:explosion_decay"
            }
          ]
          </#if>
        }
      ]
    }
    </#if>
  ]
  </#if>
}
