<#-- @formatter:off -->
<#include "mcitems.ftl">
{
    <#if data.group?has_content>"group": "${data.group}",</#if>
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