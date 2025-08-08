<#include "mcitems.ftl">
<#include "mcelements.ftl">
{
  "root_state_provider": ${mappedBlockToBlockStateProvider(input$root_state_provider)},
  "root_radius": ${field$root_radius},
  "root_column_max_height": ${field$root_column_max_height},
  "root_placement_attempts": ${field$root_placement_attempts},
  "root_replaceable": "#${field$root_replaceable}",
  "feature": ${toPlacedFeature(input_id$feature, input$feature)},
  "required_vertical_space_for_tree": ${field$required_vertical_space_for_tree},
  "allowed_vertical_water_for_tree": ${field$allowed_vertical_water_for_tree},
  "allowed_tree_position": ${input$allowed_tree_position},
  "hanging_root_state_provider": ${mappedBlockToBlockStateProvider(input$hanging_root_state_provider)},
  "hanging_root_radius": ${field$hanging_root_radius},
  "hanging_roots_vertical_span": ${field$hanging_roots_vertical_span},
  "hanging_root_placement_attempts": ${field$hanging_root_placement_attempts}
}