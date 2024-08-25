<#include "mcitems.ftl">
{
  "type": "minecraft:mangrove_root_placer",
  "root_provider": ${mappedBlockToBlockStateProvider(input$root_provider)},
  "trunk_offset_y": ${input$trunk_offset_y},
  <#if field$above_root_placement_chance != "0">
  "above_root_placement": {
    "above_root_placement_chance": ${field$above_root_placement_chance},
    "above_root_provider": ${mappedBlockToBlockStateProvider(input$above_root_provider)}
  },
  </#if>
  "mangrove_root_placement": {
    "can_grow_through": ${input$can_grow_through},
    "muddy_roots_in": ${input$muddy_roots_in},
    "muddy_roots_provider": ${mappedBlockToBlockStateProvider(input$muddy_roots_provider)},
    "max_root_length": ${field$max_root_length},
    "max_root_width": ${field$max_root_width},
    "random_skew_chance": ${field$random_skew_chance}
  }
}