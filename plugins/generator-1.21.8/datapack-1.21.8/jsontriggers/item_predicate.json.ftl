${input$item},
"count": {
  "min": ${field$min},
  "max": ${field$max}
}<#if input_list$predicateComponent?has_content>,
"predicates": {
  ${statement$components?remove_ending(",")}
}
</#if>