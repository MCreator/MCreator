<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "format_version": "1.20.10",
  "minecraft:recipe_furnace": {
    "description": {
      "identifier": "${data.getNamespace()}:${data.getName()}"
    },
    "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
    "tags": [ "smoker" ],
    "unlock": ${recipeUnlockJSON(data.unlockingItems)},
    "input": {
      ${mappedMCItemToItemObjectJSON(data.smokingInputStack, true)}
    },
    "output": {
      ${mappedMCItemToItemObjectJSON(data.smokingReturnStack)}
    }
  }
}
<#-- @formatter:on -->