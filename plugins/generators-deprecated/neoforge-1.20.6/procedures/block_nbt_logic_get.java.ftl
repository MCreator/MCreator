<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity=world.getBlockEntity(pos);
		if(blockEntity != null) return blockEntity.getPersistentData().getBoolean(tag);
		return false;
	}
}.getValue(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$tagName}))
<#-- @formatter:on -->