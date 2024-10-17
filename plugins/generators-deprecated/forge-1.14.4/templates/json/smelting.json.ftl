<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "group": "<#if data.group?has_content>${data.group}<#else>${modid}</#if>",
    "type": "minecraft:smelting",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.smeltingInputStack)}
    },
    "result": {
      ${mappedMCItemToIngameItemName(data.smeltingReturnStack)}
    }
}
<#-- @formatter:on -->