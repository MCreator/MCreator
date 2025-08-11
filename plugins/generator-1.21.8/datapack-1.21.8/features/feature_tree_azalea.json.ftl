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
    "type": "minecraft:bending_trunk_placer",
    "base_height": ${field$base_height},
    "height_rand_a": ${field$height_variation_a},
    "height_rand_b": ${field$height_variation_b},
    "bend_length": ${input$bend_length},
    "min_height_for_leaves": ${field$min_height_for_leaves}
  },
  "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=0 upper_size=1/>,
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}