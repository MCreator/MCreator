<#include "mcitems.ftl">
<#include "trees.ftl">
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