<#include "mcitems.ftl">
<#include "trees.ftl">
{
  "dirt_provider": ${mappedBlockToBlockStateProvider(input$dirt)},
  "foliage_provider": ${mappedBlockToBlockStateProvider(input$foliage)},
  "trunk_provider": ${mappedBlockToBlockStateProvider(input$trunk)},
  <#if input_id$root_placer != "root_placer_none">
  "root_placer": ${input$root_placer},
  </#if>
  "force_dirt": ${field$force_dirt?lower_case},
  "ignore_vines": ${field$ignore_vines?lower_case},
  "foliage_placer": {
    "type": "minecraft:cherry_foliage_placer",
    "height": ${field$foliage_height},
    "offset": 0,
    "radius": 4,
    "corner_hole_chance": ${field$corner_hole_chance},
    "hanging_leaves_chance": ${field$hanging_leaves_chance},
    "hanging_leaves_extension_chance": ${field$hanging_leaves_extension_chance},
    "wide_bottom_layer_hole_chance": ${field$bottom_hole_chance}
  },
  "trunk_placer": {
    "type": "minecraft:cherry_trunk_placer",
    "base_height": ${field$base_height},
    "height_rand_a": ${field$height_variation_a},
    "height_rand_b": ${field$height_variation_b},
    "branch_count": ${input$branch_count},
    "branch_horizontal_length": ${input$branch_length},
    "branch_end_offset_from_top": {
      "type": "minecraft:uniform",
      "value": {
        "min_inclusive": -1,
        "max_inclusive": 0
      }
    },
    "branch_start_offset_from_top": {
      "min_inclusive": -4,
      "max_inclusive": -3
    }
  },
  "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=0 upper_size=2/>,
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}