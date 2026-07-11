<#include "mcelements.ftl">
{
	BlockPos _pos = ${toBlockPos(input$x,input$y,input$z)};
	Block.dropResources(world.getBlockState(_pos), world, ${toBlockPos(input$x2,input$y2,input$z2)}, null);
	world.destroyBlock(_pos, false);
}