"${registryname}_${cbi}": {
  "trigger": "minecraft:inventory_changed",
  "conditions": {
    <#if input$player?has_content>
    "player": ${input$player}
    </#if>
    "items": [
      {
        ${input$item}
      }
    ]
  }
},