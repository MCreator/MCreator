<#include "../mcitems.ftl">
<#assign isFlowerPot = data.getModElement().getTypeString() == "block" && data.blockBase! == "FlowerPot">
<#assign isSlab = data.getModElement().getTypeString() == "block" && data.blockBase! == "Slab">
<#assign isLeaves = data.getModElement().getTypeString() == "block" && data.blockBase! == "Leaves">
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
          <#elseif isLeaves>, <#-- Use vanilla leaves dropping logic -->
          "conditions": [
            {
              "condition": "minecraft:table_bonus",
              "enchantment": "minecraft:fortune",
              "chances": [ 0.05, 0.0625, 0.083333336, 0.1 ]
            }
          ]
          </#if>
          <#if data.dropAmount != 1 || isSlab>, <#-- Handle cases where block can drop more than one item -->
          "functions": [
            <#if data.dropAmount != 1>
            {
              "function": "minecraft:set_count",
              "count": ${data.dropAmount}
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
