<#include "mcitems.ftl">
{
    "replace": false,
    "values": [
      <#if type == "items" || type == "blocks">
          <#list w.filterBrokenReferences(elements) as value>
            "${mappedMCItemToRegistryName(value, true)}"<#sep>,
          </#list>
      <#elseif type == "entities">
          <#list w.filterBrokenReferences(elements) as value>
            "${generator.map(value.getUnmappedValue(), "entities", 2)}"<#sep>,
          </#list>
      <#elseif type == "biomes">
          <#list w.filterBrokenReferences(elements) as value>
            "${value.getUnmappedValue()}"<#sep>,
          </#list>
      <#elseif type == "functions">
          <#list w.filterBrokenReferences(elements) as value>
            "${generator.getResourceLocationForModElement(value)}"<#sep>,
          </#list>
      <#elseif type == "damage_types">
          <#list w.filterBrokenReferences(elements) as value>
            "${generator.map(value.getUnmappedValue(), "damagesources", 1)}"<#sep>,
          </#list>
      </#if>
    ]
}