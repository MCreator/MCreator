<#include "mcelements.ftl">
if(!world.isClientSide()) {
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	BlockEntity _blockEntity = world.getBlockEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_blockEntity != null)
		_blockEntity.getPersistentData().putDouble(${input$tagName}, ${input$tagValue});

	if(world instanceof Level _level)
		_level.sendBlockUpdated(_bp, _bs, _bs, 3);
}