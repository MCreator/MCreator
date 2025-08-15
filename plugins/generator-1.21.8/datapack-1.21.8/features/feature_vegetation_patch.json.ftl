<#include "mcitems.ftl">
<#include "mcelements.ftl">
{
  "surface": ${field$surface},
  "xz_radius": ${input$xz_radius},
  "vertical_range": ${field$vertical_range},
  "vegetation_feature": ${toPlacedFeature(input_id$vegetation_feature, input$vegetation_feature)},
  "vegetation_chance": ${field$vegetation_chance},
  "ground_state": ${mappedBlockToBlockStateProvider(input$ground_state)},
  "replaceable": "#${field$replaceable}",
  "depth": ${input$depth},
  "extra_bottom_block_chance": ${field$extra_bottom_block_chance},
  "extra_edge_column_chance": ${field$extra_edge_column_chance}
}