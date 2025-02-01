<#include "../mcitems.ftl">
<#assign defaultSlabDrops = data.getModElement().getTypeString() == "block" && data.blockBase?has_content && data.blockBase == "Slab" && !(data.customDrop?? && !data.customDrop.isEmpty())/>
{
  "type": "minecraft:block",
  "random_sequence": "${modid}:blocks/${registryname}"
  <#if data.dropAmount != 0>,
  "pools": [
    {
      "rolls": 1.0,
      <#if data.dropAmount == 1 && !defaultSlabDrops>
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ],
      </#if>
      "entries": [
        {
          <#if data.customDrop?? && !data.customDrop.isEmpty()>
            <#assign dropItem = mappedMCItemToRegistryName(data.customDrop)>
            <#if data.customDrop.isAir() || dropItem == "minecraft:air">
            "type": "minecraft:empty"
            <#else>
            "type": "minecraft:item",
            "name": "${dropItem}"
            </#if>
          <#else>
          "type": "minecraft:item",
          "name": "${modid}:${registryname}"
          </#if>
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
          <#elseif defaultSlabDrops>,
          "functions": [
            {
              "conditions": [
                {
                  "condition": "minecraft:block_state_property",
                  "block": "${modid}:${registryname}",
                  "properties": {
                    "type": "double"
                  }
                }
              ],
              "count": 2.0,
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
