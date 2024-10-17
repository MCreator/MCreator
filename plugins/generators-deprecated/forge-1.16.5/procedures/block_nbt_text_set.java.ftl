<#include "mcelements.ftl">
if(!world.isRemote()) {
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	TileEntity _tileEntity=world.getTileEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_tileEntity!=null)
		_tileEntity.getTileData().putString(${input$tagName}, ${input$tagValue});

	if(world instanceof World)
		((World) world).notifyBlockUpdate(_bp, _bs, _bs, 3);
}