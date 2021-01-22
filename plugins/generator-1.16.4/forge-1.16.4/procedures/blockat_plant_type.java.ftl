<#-- @formatter:off -->
(new Object(){
	public boolean checkPlantType(IWorld world, BlockPos pos) {
		Block _block = world.getBlockState(pos).getBlock();
		return _block instanceof IPlantable ? ((IPlantable)_block).getPlantType(world, pos) == PlantType.get("${field$type}".toLowerCase(Locale.ENGLISH)) : false;
	}
}.checkPlantType(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->