<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "format_version": "1.20.10",
  "minecraft:recipe_furnace": {
    "description": {
      "identifier": "${data.getNamespace()}:${data.getName()}"
    },
    "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
    "tags": [ "campfire" ],
    "unlock": ${recipeUnlockJSON(data.unlockingItems)},
    "input": {
      ${mappedMCItemToItemObjectJSON(data.campfireCookingInputStack, true)}
    },
    "output": {
      ${mappedMCItemToItemObjectJSON(data.campfireCookingReturnStack)}
    }
  }
}
<#-- @formatter:on -->