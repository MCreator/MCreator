<#include "mcitems.ftl">
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
      <#elseif type == "biomes" || type == "structures" || type == "game_events">
          <#list w.normalizeTagElements(tag.resourcePath(), 0, elements) as value>
			<@tagEntry value value/><#sep>,
          </#list>
      <#elseif type == "damage_types" || type == "enchantments">
          <#list w.normalizeTagElements(tag.resourcePath(), 1, elements) as value>
			<@tagEntry value value.getMappedValue(1)/><#sep>,
          </#list>
      <#elseif type == "functions">
          <#list w.filterBrokenReferences(elements) as value>
			<@tagEntry value generator.getResourceLocationForModElement(value)/><#sep>,
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