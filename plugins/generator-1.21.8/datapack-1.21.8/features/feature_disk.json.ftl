<#include "mcitems.ftl">
{
  "radius": ${input$radius},
  "half_height": ${field$half_height},
  "target": ${input$target},
  "state_provider": {
    "fallback": ${mappedBlockToBlockStateProvider(input$fallback)},
    "rules": [
    <#list input_list$rule as rule>
      ${rule}
    <#sep>,</#list>
    ]
  }
}