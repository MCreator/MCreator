<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "type": "minecraft:smoking",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.smokingInputStack)}
    },
    "result": {
      ${mappedMCItemToIngameItemName(data.smokingReturnStack)}
    }
}
<#-- @formatter:on -->