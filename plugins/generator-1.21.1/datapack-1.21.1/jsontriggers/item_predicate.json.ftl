${input$item},
"count": {
  "min": ${field$min},
  "max": ${field$max}
}<#if statement$components?has_content>,
"predicates": {
  ${statement$components?remove_ending(",")}
}
</#if>