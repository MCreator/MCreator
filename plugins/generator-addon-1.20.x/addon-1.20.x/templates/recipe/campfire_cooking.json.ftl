<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "format_version": "1.12",
    "minecraft:recipe_furnace": {
      "description": {
        "identifier": "${data.getNamespace()}:${data.getName()}"
      },
      "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
      "tags": [ "campfire" ],
      "input": {
        ${mappedMCItemToItemObjectJSON(data.campfireCookingInputStack)}
      },
      "output": {
        ${mappedMCItemToItemObjectJSON(data.campfireCookingReturnStack)}
      }
    }
}
<#-- @formatter:on -->