{
  "type": "minecraft:biased_to_bottom",
  "min_inclusive": ${input$min},
  "max_inclusive": ${input$max}
  <#if field$inner != "1">, "inner": ${field$inner}</#if>
}