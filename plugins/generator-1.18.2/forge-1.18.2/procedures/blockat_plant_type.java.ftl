<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean checkPlantType(LevelAccessor world, BlockPos pos) {
		Block _block = world.getBlockState(pos).getBlock();
		return _block instanceof IPlantable _plant ? _plant.getPlantType(world, pos) == PlantType.${generator.map(field$planttype, "planttypes")} : false;
	}
}.checkPlantType(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->