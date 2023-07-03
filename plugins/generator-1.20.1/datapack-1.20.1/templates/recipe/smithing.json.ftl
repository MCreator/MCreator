<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smithing_transform",
    "template": [],
    "base": {
      ${mappedMCItemToIngameItemName(data.smithingInputStack)}
    },
    "addition": {
      ${mappedMCItemToIngameItemName(data.smithingInputAdditionStack)}
    },
    "result": {
      ${mappedMCItemToIngameItemName(data.smithingReturnStack)}
    }
}
<#-- @formatter:on -->