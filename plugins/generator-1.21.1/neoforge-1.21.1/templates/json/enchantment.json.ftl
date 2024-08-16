{
  "description": {
    "translate": "enchantment.${modid}.${registryname}"
  },
  "supported_items": "${generator.map(data.type, "enchantmenttypes", 0)}",
  "weight": ${rarityToWeight(data.rarity)},
  "max_level": ${data.maxLevel},
  "min_cost": {
    "base": 1,
    "per_level_above_first": 10
  },
  "max_cost": {
    "base": 6,
    "per_level_above_first": 10
  },
  "anvil_cost": ${rarityToAnvilCost(data.rarity)},
  "slots": [
    ${generator.map(data.type, "enchantmenttypes", 1)}
  ],
  "exclusive_set": "#minecraft:exclusive_set/damage"
  <#if data.damageModifier != 0>,
  "effects": {
    "minecraft:damage_protection": [
      {
        "effect": {
          "type": "minecraft:add",
          "value": {
            "type": "minecraft:linear",
            "base": ${data.damageModifier},
            "per_level_above_first": ${data.damageModifier}
          }
        }
      }
    ]
  }
  </#if>
}

<#function rarityToWeight rarity>
	<#if rarity == "COMMON"><#return 10>
	<#elseif rarity == "UNCOMMON"><#return 5>
	<#elseif rarity == "RARE"><#return 2>
	<#else><#return 1>
	</#if>
</#function>

<#function rarityToAnvilCost rarity>
	<#if rarity == "COMMON"><#return 1>
	<#elseif rarity == "UNCOMMON"><#return 2>
	<#elseif rarity == "RARE"><#return 4>
	<#else><#return 8>
	</#if>
</#function>