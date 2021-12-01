<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "type": "minecraft:smithing",
  "base": {
    ${mappedMCItemToIngameItemName(data.smithingInputStack)}
  },
  "addition": {
    ${mappedMCItemToIngameItemName(data.smithingInputAdditionStack)}
  },
  "result": "${mappedMCItemToIngameNameNoTags(data.smithingReturnStack)}"
}
<#-- @formatter:on -->