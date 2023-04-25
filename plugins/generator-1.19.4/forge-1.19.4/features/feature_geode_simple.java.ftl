<#include "mcitems.ftl">
<#assign scale = field$size?number>
new GeodeConfiguration(
    new GeodeBlockSettings(
        ${mappedBlockToBlockStateProvider(input$filling)},
        ${mappedBlockToBlockStateProvider(input$inner_layer)},
        ${mappedBlockToBlockStateProvider(input$alternate_inner_layer)},
        ${mappedBlockToBlockStateProvider(input$middle_layer)},
        ${mappedBlockToBlockStateProvider(input$outer_layer)},
        List.of(<#list input_list$crystal as crystal>${mappedBlockToBlockStateCode(crystal)}<#sep>,</#list>),
        BlockTags.create(new ResourceLocation("${field$cannot_replace_tag}")),
        BlockTags.create(new ResourceLocation("${field$invalid_blocks_tag}"))),
    new GeodeLayerSettings(${1.7 * scale}, ${2.2 * scale}, ${3.2 * scale}, ${4.2 * scale}),
    new GeodeCrackSettings(0.95, 2.0, 2), 0.35, 0.083, true, UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), -16, 16, 0.05, ${field$invalid_blocks_count})