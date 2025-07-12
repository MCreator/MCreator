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
  <#if field$type == "pine">
    "foliage_placer": {
      "type": "minecraft:pine_foliage_placer",
      "offset": 1,
      "radius": 1,
      "height": ${input$foliage_height}
    },
    "trunk_placer": <@simpleTrunkPlacer "minecraft:straight_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=2 lower_size=0 upper_size=2/>,
  <#elseif field$type == "mega pine">
    "foliage_placer": {
      "type": "minecraft:mega_pine_foliage_placer",
      "offset": 0,
      "radius": 0,
      "crown_height": ${input$foliage_height}
    },
    "trunk_placer": <@simpleTrunkPlacer "minecraft:giant_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=1 upper_size=2/>,
  </#if>
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}