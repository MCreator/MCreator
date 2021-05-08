<#include "mcitems.ftl">
if (world instanceof ServerWorld) {
    FallingBlockEntity blockToSpawn = new FallingBlockEntity((World) world, ${input$x}, ${input$y}, ${input$z}, ${mappedBlockToBlockStateCode(input$block)});
    blockToSpawn.fallTime = 1;
    world.addEntity(blockToSpawn);
}