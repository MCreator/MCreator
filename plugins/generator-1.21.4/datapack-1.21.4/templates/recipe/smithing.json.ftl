<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smithing_transform",
    <#if data.smithingInputTemplateStack?? && !data.smithingInputTemplateStack.isEmpty()>
    "template": "${mappedMCItemToRegistryName(data.smithingInputTemplateStack, true)}",
    <#else>
    "template": [],
    </#if>
    "base": "${mappedMCItemToRegistryName(data.smithingInputStack, true)}",
    "addition": "${mappedMCItemToRegistryName(data.smithingInputAdditionStack, true)}",
    "result": {
        ${mappedMCItemToItemObjectJSON(data.smithingReturnStack, "id")}
    }
}
<#-- @formatter:on -->