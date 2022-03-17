<#-- @formatter:off -->
(new Object(){
	public String getValue(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity=world.getBlockEntity(pos);
		if(blockEntity != null) return blockEntity.getTileData().getString(tag);
		return "";
	}
}.getValue(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${input$tagName}))
<#-- @formatter:on -->