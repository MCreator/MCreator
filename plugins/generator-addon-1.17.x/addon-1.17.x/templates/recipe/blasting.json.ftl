<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "format_version": "1.12",
    "minecraft:recipe_furnace": {
      "description": {
        "identifier": "${data.getNamespace()}:${data.getName()}"
      },
      "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
      "tags": [ "blast_furnace" ],
      "input": {
        ${mappedMCItemToIngameItemName(data.blastingInputStack)}
      },
      "output": {
        ${mappedMCItemToIngameItemName(data.blastingReturnStack)}
      }
    }
}
<#-- @formatter:on -->