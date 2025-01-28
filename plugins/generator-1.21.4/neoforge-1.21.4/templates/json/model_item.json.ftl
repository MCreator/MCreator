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
	<#elseif model.property?? && model.property.getName() == "trim_type">
	<#local values = {}>
	<#list model.values.entrySet()?map(e -> {"key": e.getKey(), "value": e.getValue()})?sort_by("key") as entry>
		<#if entry["key"] lte 0.1>
			<#local values = (values["quartz"]??)?then(values, values + {"quartz": entry["value"]})>
		<#elseif entry["key"] lte 0.2>
			<#local values = (values["iron"]??)?then(values, values + {"iron": entry["value"]})>
		<#elseif entry["key"] lte 0.3>
			<#local values = (values["netherite"]??)?then(values, values + {"netherite": entry["value"]})>
		<#elseif entry["key"] lte 0.4>
			<#local values = (values["redstone"]??)?then(values, values + {"redstone": entry["value"]})>
		<#elseif entry["key"] lte 0.5>
			<#local values = (values["copper"]??)?then(values, values + {"copper": entry["value"]})>
		<#elseif entry["key"] lte 0.6>
			<#local values = (values["gold"]??)?then(values, values + {"gold": entry["value"]})>
		<#elseif entry["key"] lte 0.7>
			<#local values = (values["emerald"]??)?then(values, values + {"emerald": entry["value"]})>
		<#elseif entry["key"] lte 0.8>
			<#local values = (values["diamond"]??)?then(values, values + {"diamond": entry["value"]})>
		<#elseif entry["key"] lte 0.9>
			<#local values = (values["lapis"]??)?then(values, values + {"lapis": entry["value"]})>
		<#else>
			<#local values = (values["amethyst"]??)?then(values, values + {"amethyst": entry["value"]})>
		</#if>
	</#list>
{
  "type": "minecraft:select",
  "cases": [
	<#list values as key, value>
    {
      "model": <@modelCode value />,
      "when": "minecraft:${key}"
    }<#sep>,
	</#list>
  ],
  "fallback": <@modelCode model.fallback />,
  "property": "minecraft:trim_material"
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