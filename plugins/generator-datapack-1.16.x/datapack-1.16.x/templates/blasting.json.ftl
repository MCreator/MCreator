<#-- @formatter:off -->
<#include "mcitems.ftl">
{
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "type": "minecraft:blasting",
    "experience": ${data.xpReward},
	"cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToIngameItemName(data.blastingInputStack)}
    },
    "result": {
      ${mappedMCItemToIngameItemName(data.blastingReturnStack)}
    }
}
<#-- @formatter:on -->