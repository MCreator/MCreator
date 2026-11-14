"${registryname}_${cbi}": {
  "trigger": "minecraft:effects_changed",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      "type": "minecraft:all_of",
      "terms": [
        ${input$player}
      ]
    },
    </#if>
    "effects": {
      <#list input_list$effect as effect>
      ${effect}<#sep>,
      </#list>
    }
  }
},