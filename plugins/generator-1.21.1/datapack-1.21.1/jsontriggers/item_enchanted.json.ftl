"${registryname}_${cbi}": {
  "trigger": "minecraft:enchanted_item",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "item": {
      ${input$item}
    },
    "levels": {
    "min": ${input$levelsSpent}
    }
  }
},