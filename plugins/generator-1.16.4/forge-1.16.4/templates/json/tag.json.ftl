<#include "../mcitems.ftl">
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
      <#elseif data.type == "Entities">
          <#list data.entities as value>
            <#if value.getUnmappedValue().startsWith("CUSTOM:")>
            "${generator.getResourceLocationForModElement(value)}"
            <#if value?has_next>,</#if>
            <#else>
            "${generator.map(value, "entities", 2)}"<#if value?has_next>,</#if>
            </#if>
          </#list>
      </#if>
    ]
}