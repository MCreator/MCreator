"item": {
	"items": ${input$item},
	"count": {
		"min": ${field$min},
		"max": ${field$max}
	}<#if input_list$enchantment?has_content>,
	"predicates": {
		"enchantments": [
			<#list input_list$enchantment as enchantment>
				${enchantment}<#sep>,
			</#list>
		]
	}
	</#if>
}