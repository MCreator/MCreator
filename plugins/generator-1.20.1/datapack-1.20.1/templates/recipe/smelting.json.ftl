<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smelting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.smeltingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.smeltingReturnStack)}"
}
<#-- @formatter:on -->