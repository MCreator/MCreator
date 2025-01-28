<#assign defaultState = data.getBaseState()>
{
  "model": <@modelCode data.getModelsTree() />
}

<#macro modelCode model>
	<#if model.property?? && model.property.getName() == "damaged">
{
  "type": "minecraft:condition",
  "on_false": <@modelCode model.values.getOrDefault(false, model.fallback) />,
  "on_true": <@modelCode model.values.getOrDefault(true, defaultState) />,
  "property": "minecraft:damaged"
}
	<#elseif model.property?? && model.property.getName() == "lefthanded">
{
  "type": "minecraft:select",
  "cases": [
    {
      "model": <@modelCode model.values.getOrDefault(true, defaultState) />,
      "when": "left"
    }
  ],
  "fallback": <@modelCode model.values.getOrDefault(false, model.fallback) />,
  "property": "minecraft:main_hand"
}
	<#elseif model.property??>
{
  "type": "minecraft:range_dispatch",
  "entries": [
    <#list model.values.entrySet() as entry>
    {
      "model": <@modelCode entry.getValue() />,
      "threshold": ${entry.getKey()}
    }<#sep>,
    </#list>
  ],
  "fallback": <@modelCode model.fallback />,
  "property": "${generator.map(model.property.getPrefixedName(registryname + "/"), "itemproperties")}"
}
	<#else>
{
  "type": "minecraft:model",
  <#if model.indexString??>
  "model": "${modid}:item/${registryname}${model.indexString()}"
  <#elseif var_sufix??>
  "model": "${modid}:item/${registryname}${var_sufix}"
  <#else>
  "model": "${modid}:item/${registryname}"
  </#if>
}
	</#if>
</#macro>