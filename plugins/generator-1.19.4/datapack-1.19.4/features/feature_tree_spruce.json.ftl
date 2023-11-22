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
    "type": "minecraft:spruce_foliage_placer",
    "offset": {
      "type": "minecraft:uniform",
      "value": {
        "min_inclusive": 0,
        "max_inclusive": 2
      }
    },
    "radius": ${input$radius},
    "trunk_height": ${input$trunk_height}
  },
  "trunk_placer": <@simpleTrunkPlacer "minecraft:straight_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
  "minimum_size": <@twoLayersFeatureSize limit=2 lower_size=0 upper_size=2/>,
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}