<#include "mcitems.ftl">
{
    "replace": false,
    "values": [
      <#if data.type == "Items">
          <#list w.filterBrokenReferences(data.items) as value>
            "${mappedMCItemToIngameNameNoTags(value)}"<#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Blocks">
          <#list w.filterBrokenReferences(data.blocks) as value>
            "${mappedMCItemToIngameNameNoTags(value)}"<#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Functions">
          <#list data.functions as value>
            "${generator.getResourceLocationForModElement(value)}"
            <#if value?has_next>,</#if>
          </#list>
      </#if>
    ]
}