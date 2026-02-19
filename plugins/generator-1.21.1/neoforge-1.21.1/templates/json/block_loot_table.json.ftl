<#include "../mcitems.ftl">
<#assign isFlowerPot = data.getModElement().getTypeString() == "block" && data.blockBase! == "FlowerPot">
<#assign isSlab = data.getModElement().getTypeString() == "block" && data.blockBase! == "Slab">
<#assign isLeaves = data.getModElement().getTypeString() == "block" && data.blockBase! == "Leaves">
<#assign hasAlternatives = data.hasDefaultDrop() && (data.dropsWithSilkTouch() || data.dropsWithShears())> <#-- True if silk touch or shears change the loot -->
{
  "type": "minecraft:block",
  "random_sequence": "${modid}:blocks/${registryname}"
  <#if data.hasDefaultDropPool() || isFlowerPot || isLeaves>,
  "pools": [
    <#-- First, handle "hardcoded" drops (flower pot for potted plants, sticks for leaves...) -->
    <#if isFlowerPot> <#-- Handle flower pot drop -->
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
    }<#if data.hasDefaultDropPool()>,</#if>
    <#elseif isLeaves> <#-- Handle sticks drops -->
    {
      "rolls": 1.0,
      "conditions": [
        {
          "condition": "minecraft:inverted",
          "term": <@silkTouchOrShearsCondition data.dropsWithSilkTouch() data.dropsWithShears()/>
        }
      ],
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:stick",
          "conditions": [
            {
              "condition": "minecraft:table_bonus",
              "enchantment": "minecraft:fortune",
              "chances": [ 0.02, 0.022222223, 0.025, 0.033333335, 0.1 ]
            }
          ],
          "functions": [
            {
              "function": "minecraft:set_count",
              "count": {
                "type": "minecraft:uniform",
                "min": 1,
                "max": 2
              }
            },
            {
              "function": "minecraft:explosion_decay"
            }
          ]
        }
      ]
    }<#if data.hasDefaultDropPool()>,</#if>
    </#if>
    <#-- Then, handle the "default" drop -->
    <#if data.hasDefaultDropPool()>
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
        <#if hasAlternatives> <#-- Open the "alternatives" entry -->
        {
          "type": "minecraft:alternatives",
          "children": [
        </#if>
        <#if isLeaves && data.hasBlockItem> <#-- Entry for leaves drop with silk touch or shears -->
          {
            "type": "minecraft:item",
            "name": "${modid}:${registryname}",
            "conditions": [ <@silkTouchOrShearsCondition data.dropsWithSilkTouch() data.dropsWithShears()/> ]
          }
          <#if data.hasDefaultDrop()>,</#if>
        </#if>
        <#if data.hasDefaultDrop()> <#-- Entry for the default block drop -->
        {
          "type": "minecraft:item",
          "name": "${mappedMCItemToRegistryName(data.getDefaultDrop())}"
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
        </#if>
        <#if hasAlternatives> <#-- Close the "alternatives" entry -->
        ]}
        </#if>
      ]
    }
    </#if>
  ]
  </#if>
}

<#macro silkTouchOrShearsCondition silkTouch shears>
<#if silkTouch && shears> <#-- Open the "Any of" predicate if both options are selected -->
{
  "condition": "minecraft:any_of",
  "terms": [
</#if>
    <#if shears>
    {
      "condition": "minecraft:match_tool",
      "predicate": {
        "items": "minecraft:shears"
      }
    }<#if silkTouch>,</#if>
    </#if>
    <#if silkTouch>
    {
      "condition": "minecraft:match_tool",
      "predicate": {
        "predicates": {
          "minecraft:enchantments": [
            {
              "enchantments": "minecraft:silk_touch",
              "levels": {
                "min": 1
              }
            }
          ]
        }
      }
    }
    </#if>
<#if silkTouch && shears>
  ]
}
</#if>
</#macro>