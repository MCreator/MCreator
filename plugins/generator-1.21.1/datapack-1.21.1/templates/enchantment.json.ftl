<#include "mcitems.ftl">

<#assign supportedItems = w.filterBrokenReferences(data.supportedItems)>
<#assign incompatibleEnchantments = w.filterBrokenReferences(data.incompatibleEnchantments)>

{
  <#if generator.getGeneratorFlavor() == "DATAPACK">
  "description": "${data.name}",
  <#else>
  "description": {
    "translate": "enchantment.${modid}.${registryname}"
  },
  </#if>
  <#if supportedItems?size == 1>
  "supported_items": "${mappedMCItemToRegistryName(supportedItems?first, true)}",
  <#else>
  "supported_items": [
    <#list supportedItems as supportedItem>"${mappedMCItemToRegistryName(supportedItem)}"<#sep>,</#list>
  ],
  </#if>
  "weight": ${data.weight},
  "max_level": ${data.maxLevel},
  "min_cost": {
    "base": 1,
    "per_level_above_first": 10
  },
  "max_cost": {
    "base": 6,
    "per_level_above_first": 10
  },
  "anvil_cost": ${data.anvilCost},
  "slots": [
    ${data.supportedSlots}
  ],
  <#if incompatibleEnchantments?size == 1>
  "exclusive_set": "${generator.map(incompatibleEnchantments?first.getUnmappedValue(), "enchantments", 1)}"
  <#else>
  "exclusive_set": [
    <#list incompatibleEnchantments as incompatibleEnchantment>"${generator.map(incompatibleEnchantment.getUnmappedValue(), "enchantments", 1)}"<#sep>,</#list>
  ]
  </#if>
  <#if data.damageModifier != 0>,
  "effects": {
  	<#if data.damageModifier gt 0>
    "minecraft:damage_protection"
    <#else>
    "minecraft:damage"
    </#if>
    :[
      {
        "effect": {
          "type": "minecraft:add",
          "value": {
            "type": "minecraft:linear",
            "base": ${data.damageModifier?abs},
            "per_level_above_first": ${data.damageModifier?abs}
          }
        }
      }
    ]
  }
  </#if>
}