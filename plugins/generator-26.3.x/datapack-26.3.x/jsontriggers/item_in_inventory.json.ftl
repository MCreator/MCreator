"${registryname}_${cbi}": {
  "trigger": "minecraft:inventory_changed",
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
    "items": [
      {
        ${input$item}
      }
    ]
  }
},