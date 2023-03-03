<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean getValue(LevelAccessor _world, BlockPos _pos, String _tag) {
		BlockEntity _blockEntity = _world.getBlockEntity(_pos);
		if(_blockEntity != null) return _blockEntity.getPersistentData().getBoolean(_tag);
		return false;
	}
}.getValue(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$tagName}))
<#-- @formatter:on -->