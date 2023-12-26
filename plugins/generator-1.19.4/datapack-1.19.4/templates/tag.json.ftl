<#include "mcitems.ftl">
<#assign tag = data.getNamespace() + ":" + data.getName()>
{
    "replace": false,
    "values": [
      <#if data.type == "Items">
          <#list w.normalizeTagElements(tag, 1, data.items) as value>
            "${mappedMCItemToRegistryName(value, true)}"<#sep>,
          </#list>
      <#elseif data.type == "Blocks">
          <#list w.normalizeTagElements(tag, 1, data.blocks) as value>
            "${mappedMCItemToRegistryName(value, true)}"<#sep>,
          </#list>
      <#elseif data.type == "Functions">
          <#list data.functions as value>
            "${generator.getResourceLocationForModElement(value)}"<#sep>,
          </#list>
      <#elseif data.type == "Entities">
          <#list w.normalizeTagElements(tag, 2, data.entities) as value>
            "${value.getMappedValue(2)}"<#sep>,
          </#list>
      <#elseif data.type == "Biomes">
          <#list w.normalizeTagElements(tag, 0, data.biomes) as value>
            "${value}"<#sep>,
          </#list>
      <#elseif data.type == "Damage types">
          <#list w.normalizeTagElements(tag, 1, data.damageTypes) as value>
            "${value.getMappedValue(1)}"<#sep>,
          </#list>
      </#if>
    ]
}