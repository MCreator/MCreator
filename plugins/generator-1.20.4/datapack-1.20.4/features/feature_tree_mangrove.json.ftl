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
  "foliage_placer": <@randomSpreadFoliagePlacer radius=3 offset=0 foliage_height=2 density=field$foliage_density/>,
  "trunk_placer": {
    "type": "minecraft:upwards_branching_trunk_placer",
    "base_height": ${field$base_height},
    "height_rand_a": ${field$height_variation_a},
    "height_rand_b": ${field$height_variation_b},
    "can_grow_through": ${input$can_grow_through},
    "place_branch_per_log_probability": ${field$branch_per_log_probability},
    "extra_branch_length": {
      "type": "minecraft:uniform",
      "value": {
        "min_inclusive": 0,
        "max_inclusive": 1
      }
    },
    "extra_branch_steps": {
      "type": "minecraft:uniform",
      "value": {
        "min_inclusive": 1,
        "max_inclusive": <#if field$type == "mangrove">4<#else>6</#if>
      }
    }
  },
  <#if field$type == "mangrove">
    "minimum_size": <@twoLayersFeatureSize limit=2 lower_size=0 upper_size=2/>,
  <#elseif field$type == "tall mangrove">
    "minimum_size": <@twoLayersFeatureSize limit=3 lower_size=0 upper_size=2/>,
  </#if>
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}