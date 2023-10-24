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
  <#if field$type == "oak">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:blob_foliage_placer" radius=2 offset=0 height=3/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:straight_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=0 upper_size=1/>,
  <#elseif field$type == "acacia">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:acacia_foliage_placer" radius=2 offset=0/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:forking_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=0 upper_size=2/>,
  <#elseif field$type == "dark oak">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:dark_oak_foliage_placer" radius=0 offset=0/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:dark_oak_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@threeLayersFeatureSize limit=1 upper_limit=1 lower_size=0 middle_size=1 upper_size=2/>,
  <#elseif field$type == "jungle bush">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:bush_foliage_placer" radius=2 offset=1 height=2/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:straight_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=0 lower_size=0 upper_size=0/>,
  <#elseif field$type == "mega jungle">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:jungle_foliage_placer" radius=2 offset=0 height=2/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:mega_jungle_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=1 lower_size=1 upper_size=2/>,
  <#elseif field$type == "fancy oak">
    "foliage_placer": <@simpleFoliagePlacer type="minecraft:fancy_foliage_placer" radius=2 offset=4 height=4/>,
    "trunk_placer": <@simpleTrunkPlacer "minecraft:fancy_trunk_placer" field$base_height field$height_variation_a field$height_variation_b/>,
    "minimum_size": <@twoLayersFeatureSize limit=0 lower_size=0 upper_size=0 min_clipped_height=4/>,
  </#if>
  "decorators": [
    <#list input_list$decorator as decorator>
      ${decorator}
    <#sep>,</#list>
  ]
}