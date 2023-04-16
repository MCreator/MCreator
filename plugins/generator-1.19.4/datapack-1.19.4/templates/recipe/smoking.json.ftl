<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smoking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.smokingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.smokingReturnStack)}"
}
<#-- @formatter:on -->