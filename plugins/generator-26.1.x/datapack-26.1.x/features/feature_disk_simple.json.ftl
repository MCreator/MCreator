<#include "mcitems_json.ftl">
{
  "radius": ${input$radius},
  "half_height": ${field$half_height},
  "target": ${input$target},
  "state_provider": ${mappedBlockToBlockStateProvider(input$block)}
}