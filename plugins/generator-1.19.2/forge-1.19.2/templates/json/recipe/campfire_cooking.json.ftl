<#-- @formatter:off -->
<#include "../../mcitems.ftl">
{
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "type": "minecraft:campfire_cooking",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.campfireCookingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.campfireCookingReturnStack)}"
}
<#-- @formatter:on -->