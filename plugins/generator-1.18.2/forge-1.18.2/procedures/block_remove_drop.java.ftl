<#include "mcelements.ftl">
if(world instanceof Level) {
    Block.dropResources(world.getBlockState(
        ${toBlockPos(input$x,input$y,input$z)}), (Level) world, ${toBlockPos(input$x2,input$y2,input$z2)});

    world.destroyBlock(${toBlockPos(input$x,input$y,input$z)}, false);
}