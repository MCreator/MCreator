<#include "mcitems_json.ftl">
{
    "replace": false,
    "values": [
      <#if type == "items" || type == "blocks">
          <#list w.normalizeTagElements(tag.resourcePath(), 1, elements) as value>
            <@tagEntry value mappedMCItemToRegistryName(value, true)/><#sep>,
          </#list>
      <#elseif type == "entities">
          <#list w.normalizeTagElements(tag.resourcePath(), 2, elements) as value>
            <@tagEntry value value.getMappedValue(2)/><#sep>,
          </#list>
      <#elseif type == "biomes" || type == "structures">
          <#list w.normalizeTagElements(tag.resourcePath(), 0, elements) as value>
            <@tagEntry value value/><#sep>,
          </#list>
      <#elseif type == "damage_types" || type == "enchantments" || type == "game_events">
          <#list w.normalizeTagElements(tag.resourcePath(), 1, elements) as value>
            <@tagEntry value value.getMappedValue(1)/><#sep>,
          </#list>
      <#elseif type == "functions">
          <#list w.filterBrokenReferences(elements) as value>
            <@tagEntry value generator.getResourceLocationForModElement(value)/><#sep>,
          </#list>
      <#elseif type == "villager_trades">
          <#list elements as value>
            "${modid}:${tag.getName()?keep_before("/")}/${tradeFolder(tag.getName()?keep_after("/"))}/${value}"<#sep>,
          </#list>
      <#else>
          <#list w.filterBrokenReferences(elements) as value>
            <@tagEntry value generator.getResourceLocationForModElement(value?remove_beginning("CUSTOM:"))/><#sep>,
          </#list>
      </#if>
    ]
}

<#macro tagEntry valueObject name>
    <#assign value = valueObject.getUnmappedValue()>
    <#-- make external entries and tag entries optional -->
    <#if value?starts_with("EXTERNAL:") || value?starts_with("TAG:") || value?starts_with("#")>
		{
          "id": "${name}",
          "required": false
        }
    <#else>
		"${name}"
    </#if>
</#macro>

<#function tradeFolder suffix>
    <#if suffix == "common"><#return "1">
    <#elseif suffix == "uncommon"><#return "2">
    <#elseif suffix == "buying"><#return "3">
    <#else><#return suffix[suffix?length-1]>
    </#if>
</#function>