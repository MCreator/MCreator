<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "format_version": "1.12",
    "minecraft:recipe_shapeless": {
      "description": {
        "identifier": "${data.getNamespace()}:${data.getName()}"
      },
      "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
      "priority": 0,
      "tags": [ "stonecutter" ],
      "ingredients": [
        {
            ${mappedMCItemToItemObjectJSON(data.stoneCuttingInputStack)}
        }
      ],
      "result": {
        ${mappedMCItemToItemObjectJSON(data.stoneCuttingReturnStack)},
        "count": ${data.recipeRetstackSize}
      }
    }
}
<#-- @formatter:on -->