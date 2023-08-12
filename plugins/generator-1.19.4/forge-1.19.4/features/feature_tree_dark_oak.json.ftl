<#include "mcitems.ftl">
<#include "trees.ftl">
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