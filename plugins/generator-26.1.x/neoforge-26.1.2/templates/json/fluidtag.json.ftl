{
	"replace": false,
	"values": [
		<#assign elements = []>
		<#list w.getElementsOfType("fluid") as mod>
			<#if mod.getGeneratableElement().type.toString()?lower_case == var_type>
				<#assign elements += ["${modid}:${mod.getRegistryName()}"]>
				<#assign elements += ["${modid}:flowing_${mod.getRegistryName()}"]>
			</#if>
		</#list>

		<#list elements as e>
			"${e}"<#sep>,
		</#list>
	]
}