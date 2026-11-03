"${registryname}_${cbi}": {
  "trigger": "minecraft:recipe_crafted",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      "type": "minecraft:all_of",
      "terms": [
        ${input$player}
      ]
    },
    </#if>
    "recipes": "${field$recipe}"
  }
},