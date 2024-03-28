<#include "mcitems.ftl">
{
  "type": "minecraft:attached_to_leaves",
  "block_provider": ${mappedBlockToBlockStateProvider(input$provider)},
  "probability": ${field$probability},
  "exclusion_radius_xz": ${field$exclusion_radius_xz},
  "exclusion_radius_y": ${field$exclusion_radius_y},
  "required_empty_blocks": ${field$required_empty_blocks},
  "directions": [
  	<#list field_list$direction as direction>"${generator.map(direction, "directions", 1)}"<#sep>,</#list>
  ]
}