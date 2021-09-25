<#include "mcitems.ftl">
if (world instanceof ServerLevel _level) {
    FallingBlockEntity blockToSpawn = new FallingBlockEntity(_level, ${input$x}, ${input$y}, ${input$z}, ${mappedBlockToBlockStateCode(input$block)});
    blockToSpawn.time = 1;
    _level.addFreshEntity(blockToSpawn);
}