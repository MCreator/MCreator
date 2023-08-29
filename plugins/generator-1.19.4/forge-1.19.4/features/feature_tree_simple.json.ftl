<#include "mcitems.ftl">
<#include "trees.ftl">
<#if field$type == "oak">
<@toTreeConfiguration
  dirt_provider=mappedBlockToBlockStateProvider(input$dirt)
  foliage_provider=mappedBlockToBlockStateProvider(input$foliage)
  trunk_provider=mappedBlockToBlockStateProvider(input$trunk)
  foliage_config=["minecraft:blob_foliage_placer", 2, 0, 3]
  trunk_config=["minecraft:straight_trunk_placer", field$base_height, field$height_variation_a, field$height_variation_b]
  size_config=[1, 0, 1]
  force_dirt=field$force_dirt?lower_case
  ignore_vines=field$ignore_vines?lower_case
  decorators=input_list$decorator
/>
<#elseif field$type == "acacia">
<@toTreeConfiguration
  dirt_provider=mappedBlockToBlockStateProvider(input$dirt)
  foliage_provider=mappedBlockToBlockStateProvider(input$foliage)
  trunk_provider=mappedBlockToBlockStateProvider(input$trunk)
  foliage_config=["minecraft:acacia_foliage_placer", 2, 0]
  trunk_config=["minecraft:forking_trunk_placer", field$base_height, field$height_variation_a, field$height_variation_b]
  size_config=[1, 0, 2]
  force_dirt=field$force_dirt?lower_case
  ignore_vines=field$ignore_vines?lower_case
  decorators=input_list$decorator
/>
<#elseif field$type == "dark oak">
<@toTreeConfiguration
  dirt_provider=mappedBlockToBlockStateProvider(input$dirt)
  foliage_provider=mappedBlockToBlockStateProvider(input$foliage)
  trunk_provider=mappedBlockToBlockStateProvider(input$trunk)
  foliage_config=["minecraft:dark_oak_foliage_placer", 0, 0]
  trunk_config=["minecraft:dark_oak_trunk_placer", field$base_height, field$height_variation_a, field$height_variation_b]
  size_config=[1, 0, 2, 1, 1]
  force_dirt=field$force_dirt?lower_case
  ignore_vines=field$ignore_vines?lower_case
  decorators=input_list$decorator
/>
<#elseif field$type == "jungle bush">
<@toTreeConfiguration
  dirt_provider=mappedBlockToBlockStateProvider(input$dirt)
  foliage_provider=mappedBlockToBlockStateProvider(input$foliage)
  trunk_provider=mappedBlockToBlockStateProvider(input$trunk)
  foliage_config=["minecraft:bush_foliage_placer", 2, 1, 2]
  trunk_config=["minecraft:straight_trunk_placer", field$base_height, field$height_variation_a, field$height_variation_b]
  size_config=[0, 0, 0]
  force_dirt=field$force_dirt?lower_case
  ignore_vines=field$ignore_vines?lower_case
  decorators=input_list$decorator
/>
<#elseif field$type == "mega jungle">
<@toTreeConfiguration
  dirt_provider=mappedBlockToBlockStateProvider(input$dirt)
  foliage_provider=mappedBlockToBlockStateProvider(input$foliage)
  trunk_provider=mappedBlockToBlockStateProvider(input$trunk)
  foliage_config=["minecraft:jungle_foliage_placer", 2, 0, 2]
  trunk_config=["minecraft:mega_jungle_trunk_placer", field$base_height, field$height_variation_a, field$height_variation_b]
  size_config=[1, 1, 2]
  force_dirt=field$force_dirt?lower_case
  ignore_vines=field$ignore_vines?lower_case
  decorators=input_list$decorator
/>
</#if>