${input$item},
"count": {
  "min": ${field$min},
  "max": ${field$max}
}<#if input_list$predicateComponent?has_content>,
"predicates": {
  <#list input_list$predicateComponent as comp>
    ${comp}
  <#sep>,</#list>
}
</#if>