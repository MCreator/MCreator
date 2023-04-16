<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:campfire_cooking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "misc", <#-- can be any value from CookingBookCategory -->
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.campfireCookingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.campfireCookingReturnStack)}"
}
<#-- @formatter:on -->