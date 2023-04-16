<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:blasting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "misc", <#-- can be any value from CookingBookCategory -->
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.blastingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.blastingReturnStack)}"
}
<#-- @formatter:on -->