${input$item}<#if field$min1?number gt 1 || field$max1?number != 64>,
"count": {
	"min": ${field$min1},
	"max": ${field$max1}
}</#if><#if input_list$predicateComponent?has_content>,
"predicates": {
	<#list input_list$predicateComponent as comp>
		${comp}
	<#sep>,</#list>
}
</#if>