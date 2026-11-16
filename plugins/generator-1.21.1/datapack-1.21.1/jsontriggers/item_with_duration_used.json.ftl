"${registryname}_${cbi}": {
  "trigger": "minecraft:using_item",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "item": {
      ${input$item}
    }
  }
},