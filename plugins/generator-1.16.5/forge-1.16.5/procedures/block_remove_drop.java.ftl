<#include "mcelements.ftl">
if(world instanceof World) {
    Block.spawnDrops(world.getBlockState(
        ${toBlockPos(input$x,input$y,input$z)}), (World) world, ${toBlockPos(input$x2,input$y2,input$z2)});

    world.destroyBlock(${toBlockPos(input$x,input$y,input$z)}, false);
}