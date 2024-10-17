<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public String getValue(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity=world.getBlockEntity(pos);
		if(blockEntity != null) return blockEntity.getTileData().getString(tag);
		return "";
	}
}.getValue(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$tagName}))
<#-- @formatter:on -->