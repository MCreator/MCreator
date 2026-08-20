<#include "mcelements.ftl">
{
  "condition": "minecraft:time_check",
  "value": {
    "min": ${levelValueToNumProvider(input_id$min, input$min)},
    "max": ${levelValueToNumProvider(input_id$max, input$max)}
  },
  "period": ${field$period}
}