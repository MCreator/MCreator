<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smelting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToItemObjectJSON(data.smeltingInputStack)}
    },
    "result": "${mappedMCItemToRegistryName(data.smeltingReturnStack)}"
}
<#-- @formatter:on -->