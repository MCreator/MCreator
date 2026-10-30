<#include "mcitems_json.ftl">
"radius": ${input$radius},
"half_height": ${field$half_height},
"target": ${input$target},
"state_provider": {
  "type": "minecraft:rule_based",
  "fallback": ${mappedBlockToBlockStateProvider(input$fallback)},
  "rules": [
  <#list input_list$rule as rule>
    ${rule}
  <#sep>,</#list>
  ]
}