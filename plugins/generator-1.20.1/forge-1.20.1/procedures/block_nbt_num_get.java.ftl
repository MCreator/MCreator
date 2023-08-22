<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public double getValue(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity=world.getBlockEntity(pos);
		if(blockEntity != null) return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}.getValue(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$tagName}))
<#-- @formatter:on -->