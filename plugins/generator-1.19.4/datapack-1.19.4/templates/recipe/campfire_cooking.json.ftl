<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:campfire_cooking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.campfireCookingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.campfireCookingReturnStack)}"
}
<#-- @formatter:on -->