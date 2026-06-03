{
  "allowed_placement": ${input$allowed_placement},
  "direction": "${generator.map(field$direction, "directions", 1)}",
  "prioritize_tip": ${field$prioritize_tip?lower_case},
  "layers": [
    <#list input_list$layer as layer>
      ${layer}
    <#sep>,</#list>
  ]
}