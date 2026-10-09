{
  "type": "minecraft:lookup",
  "values": [
    <#list field_list$value as value>${value}<#sep>,</#list>
  ],
  "fallback": ${input$fallback}
}