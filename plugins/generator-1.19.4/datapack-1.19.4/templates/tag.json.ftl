<#include "mcitems.ftl">
<#assign tag = data.getNamespace() + ":" + data.getName()>
{
    "replace": false,
    "values": [
      <#if data.type == "Items">
          <#list w.normalizeTagElements(tag, 1, data.items) as value>
            "${mappedMCItemToRegistryName(value, true)}"<#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Blocks">
          <#list w.normalizeTagElements(tag, 1, data.blocks) as value>
            "${mappedMCItemToRegistryName(value, true)}"<#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Functions">
          <#list data.functions as value>
            "${generator.getResourceLocationForModElement(value)}"
            <#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Entities">
          <#list w.normalizeTagElements(tag, 2, data.entities) as value>
            "${generator.map(value.getUnmappedValue(), "entities", 2)}"
            <#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Biomes">
          <#list w.normalizeTagElements(tag, 0, data.biomes) as value>
            "${value}"<#if value?has_next>,</#if>
          </#list>
      <#elseif data.type == "Damage types">
          <#list w.normalizeTagElements(tag, 1, data.damageTypes) as value>
            "${generator.map(value.getUnmappedValue(), "damagesources", 1)}"<#sep>,
          </#list>
      </#if>
    ]
}