<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smithing_transform",
    <#if data.smithingInputTemplateStack?? && !data.smithingInputTemplateStack.isEmpty()>
    "template": {
      ${mappedMCItemToItemObjectJSON(data.smithingInputTemplateStack)}
    },
    <#else>
    "template": [],
    </#if>
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