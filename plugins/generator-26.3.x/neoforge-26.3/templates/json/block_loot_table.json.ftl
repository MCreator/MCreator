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
      "rolls": 1,
      "condition": {
        "type": "minecraft:survives_explosion"
      },
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:flower_pot"
        }
      ]
    }<#if data.hasDefaultDropPool()>,</#if>
    <#elseif isLeaves> <#-- Handle sticks drops -->
    {
      "rolls": 1,
      <#if data.dropsWithSilkTouch() || data.dropsWithShears()>
      "condition": {
        "type": "minecraft:inverted",
        "term": <@silkTouchOrShearsCondition data.dropsWithSilkTouch() data.dropsWithShears()/>
      },
      </#if>
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:stick",
          "condition": {
            "type": "minecraft:table_bonus",
            "enchantment": "minecraft:fortune",
            "chances": [ 0.02, 0.022222223, 0.025, 0.033333335, 0.1 ]
          },
          "modifier": [
            {
              "type": "minecraft:set_count",
              "count": {
                "type": "minecraft:uniform",
                "min": 1,
                "max": 2
              }
            },
            {
              "type": "minecraft:explosion_decay"
            }
          ]
        }
      ]
    }<#if data.hasDefaultDropPool()>,</#if>
    </#if>
    <#-- Then, handle the "default" drop -->
    <#if data.hasDefaultDropPool()>
    {
      "rolls": 1,
      <#if data.dropAmount == 1 && !isSlab>
      "condition": {
        "type": "minecraft:survives_explosion"
      },
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
            "condition": <@silkTouchOrShearsCondition data.dropsWithSilkTouch() data.dropsWithShears()/>
          }
          <#if data.hasDefaultDrop()>,</#if>
        </#if>
        <#if data.hasDefaultDrop()> <#-- Entry for the default block drop -->
        {
          "type": "minecraft:item",
          "name": "${mappedMCItemToRegistryName(data.getDefaultDrop())}"
          <#if data.isDoubleBlock()>,
          "condition": {
            "type": "minecraft:match_block",
            "blocks": "${modid}:${registryname}",
            "state": {
              "half": "lower"
            }
          }
          <#elseif isLeaves>, <#-- Use vanilla leaves dropping logic -->
          "condition": {
            "type": "minecraft:table_bonus",
            "enchantment": "minecraft:fortune",
            "chances": [ 0.05, 0.0625, 0.083333336, 0.1 ]
          }
          </#if>
          <#if data.dropAmount != 1 || isSlab>, <#-- Handle cases where block can drop more than one item -->
          "modifier": [
            <#if data.dropAmount != 1>
            {
              "type": "minecraft:set_count",
              "count": ${data.dropAmount}
            },
            </#if>
            <#if isSlab> <#-- Drop twice the amount if it's a double slab -->
            {
              "type": "minecraft:set_count",
              "count": ${data.dropAmount * 2},
              "condition": {
                "type": "minecraft:match_block",
                "blocks": "${modid}:${registryname}",
                "state": {
                  "type": "double"
                }
              }
            },
            </#if>
            {
              "type": "minecraft:explosion_decay"
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
<#if silkTouch && shears> <#-- Use the "Any of" predicate if both options are selected -->
{
  "type": "minecraft:any_of",
  "terms": [ "minecraft:tool/can_shear", "minecraft:tool/can_silk_touch" ]
}
<#elseif shears>
"minecraft:tool/can_shear"
<#elseif silkTouch>
"minecraft:tool/can_silk_touch"
</#if>
</#macro>