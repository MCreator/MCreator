<#include "mcitems_json.ftl">
<#function hasToolContext>
  <#return data.type == "Block" || data.type == "Fishing" || data.type == "Generic">
</#function>
{
  "type": "minecraft:${data.type?lower_case?replace(" ", "_")}",
  "pools": [
    <#list data.pools as pool>
    {
      <#if pool.minrolls == pool.maxrolls>
      "rolls": ${pool.minrolls},
      <#else>
      "rolls": {
        "type": "minecraft:uniform",
        "min": ${pool.minrolls},
        "max": ${pool.maxrolls}
      },
      </#if>
      <#if pool.hasbonusrolls>
        <#if pool.minbonusrolls == pool.maxbonusrolls>
        "bonus_rolls": ${pool.minbonusrolls},
        <#else>
        "bonus_rolls": {
          "type": "minecraft:uniform",
          "min": ${pool.minbonusrolls},
          "max": ${pool.maxbonusrolls}
        },
        </#if>
      </#if>
      "entries": [
        <#list pool.entries as entry>
        {
          <#assign item = mappedMCItemToRegistryName(entry.item)>
          <#if entry.item.isAir() || item == "minecraft:air">
          "type": "minecraft:empty",
          <#else>
          "type": "minecraft:${entry.type}",
          "name": "${item}",
          </#if>
          "weight": ${entry.weight},
          <#if entry.silkTouchMode == 1 && hasToolContext()>
          "condition": {
              "type": "minecraft:match_tool",
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
          },
          <#elseif entry.silkTouchMode == 2 && hasToolContext()>
          "condition": {
              "type": "minecraft:inverted",
              "term": {
                "type": "minecraft:match_tool",
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
          },
          </#if>
          "modifier": [
            {
              "type": "minecraft:set_count",
              <#if entry.minCount == entry.maxCount>
              "count": ${entry.minCount}
              <#else>
              "count": {
                "type": "minecraft:uniform",
                "min": ${entry.minCount},
                "max": ${entry.maxCount}
              }
              </#if>
            }
            <#if entry.minEnchantmentLevel != 0 || entry.maxEnchantmentLevel != 0>
            ,{
              "type": "minecraft:enchant_with_levels",
              <#if entry.minEnchantmentLevel == entry.maxEnchantmentLevel>
              "levels": ${entry.minEnchantmentLevel}
              <#else>
              "levels": {
                "type": "minecraft:uniform",
                "min": ${entry.minEnchantmentLevel},
                "max": ${entry.maxEnchantmentLevel}
              }
              </#if>
            }
            </#if>
            <#if entry.explosionDecay>
            ,{
              "type": "minecraft:explosion_decay"
            }
            </#if>
            <#if entry.affectedByFortune && hasToolContext()>
            ,{
              "type": "minecraft:apply_bonus",
              "enchantment": "minecraft:fortune",
              "formula": "minecraft:ore_drops"
            }
            </#if>
          ]
        }<#sep>,</#list>
      ]
    }<#sep>,</#list>
  ],
  "random_sequence": "${data.getNamespace()}:${data.getName()}"
}
