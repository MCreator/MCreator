<#-- @formatter:off -->
<#include "../mcitems_json.ftl">
{
  "type": "minecraft:brewing",
  "input": {
    <#if var_inputitem??>
    "item": "${var_inputitem}",
    "potion_contents": {
      "potions": "${generator.map(data.brewingInputStack?replace("POTION:",""), "potions", 1)}"
    }
    <#else>
    "item": "${mappedMCItemToRegistryName(data.brewingInputStack)}"
    </#if>
  },
  "reagent": {
    "item": "${mappedMCItemToRegistryName(data.brewingIngredientStack)}"
  },
  "output": {
    <#if data.brewingReturnStack?starts_with("POTION:")>
    "id": <#if var_inputitem??>"${var_inputitem}"<#else>"minecraft:potion"</#if>,
    "components": {
      "minecraft:potion_contents": {
        "potion": "${generator.map(data.brewingReturnStack?replace("POTION:",""), "potions", 1)}"
      }
    }
    <#else>
    "id": "${mappedMCItemToRegistryName(data.brewingReturnStack)}"
    </#if>
  }
}
<#-- @formatter:on -->