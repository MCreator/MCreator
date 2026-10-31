"${registryname}_${cbi}": {
  "trigger": "minecraft:consume_item",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      "type": "minecraft:any_of",
      "terms": [
        {
          ${input$player}
        }
      ]
    },
    </#if>
    "item": {
      ${input$item}
    }
  }
},