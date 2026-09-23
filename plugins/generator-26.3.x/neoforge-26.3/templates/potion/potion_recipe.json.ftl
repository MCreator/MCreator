<#-- @formatter:off -->
{
  "type": "minecraft:brewing",
  "input": {
    "item": <#if var_base=="potion">"minecraft:potion"<#else>"minecraft:splash_potion"</#if>,
    "potion_contents": {
      "potions": "${modid}:${registryname}"
    }
  },
  "reagent": {
    "item": <#if var_base=="potion">"minecraft:gunpowder"<#else>"minecraft:dragon_breath"</#if>
  },
  "output": {
    "id": <#if var_base=="potion">"minecraft:splash_potion"<#else>"minecraft:lingering_potion"</#if>,
    "components": {
      "minecraft:potion_contents": {
        "potion": "${modid}:${registryname}"
      }
    }
  }
}
<#-- @formatter:on -->