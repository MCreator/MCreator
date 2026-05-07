<#-- @formatter:off -->
<#include "../mcitems_json.ftl">
{
    "type": "minecraft:stonecutting",
    "ingredient": "${mappedMCItemToRegistryName(data.stoneCuttingInputStack, true)}",
    "result": {
        ${mappedMCItemToItemObjectJSON(data.stoneCuttingReturnStack, "id")},
        "count": ${data.recipeRetstackSize}
    }
}
<#-- @formatter:on -->