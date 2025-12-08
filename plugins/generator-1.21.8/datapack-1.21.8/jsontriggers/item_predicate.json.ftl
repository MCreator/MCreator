${input$item}<#if field$min?number gt 1 || field$max?number != 64>,
"count": {
	"min": ${field$min},
	"max": ${field$max}
}</#if><#if input_list$predicateComponent?has_content>,
"predicates": {
	<#list input_list$predicateComponent as comp>
		${comp}
	<#sep>,</#list>
}
</#if>