"${registryname}_${cbi}": {
  "trigger": "minecraft:consume_item",
  "conditions": {
    <#if input$player?has_content>
    "player": ${input$player}
    </#if>
    "item": {
      ${input$item}
    }
  }
},