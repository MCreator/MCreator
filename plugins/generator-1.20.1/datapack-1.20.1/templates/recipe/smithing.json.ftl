<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smithing_transform",
    "template": [],
    "base": {
      ${mappedMCItemToItemObjectJSON(data.smithingInputStack)}
    },
    "addition": {
      ${mappedMCItemToItemObjectJSON(data.smithingInputAdditionStack)}
    },
    "result": {
      ${mappedMCItemToItemObjectJSON(data.smithingReturnStack)}
    }
}
<#-- @formatter:on -->