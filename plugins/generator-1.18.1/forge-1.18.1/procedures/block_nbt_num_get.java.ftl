<#-- @formatter:off -->
(new Object(){
	public double getValue(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity=world.getBlockEntity(pos);
		if(blockEntity != null) return blockEntity.getTileData().getDouble(tag);
		return -1;
	}
}.getValue(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${input$tagName}))
<#-- @formatter:on -->