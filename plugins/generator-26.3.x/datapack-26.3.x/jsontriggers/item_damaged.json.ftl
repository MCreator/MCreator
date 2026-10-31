"${registryname}_${cbi}": {
  "trigger": "minecraft:item_durability_changed",
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