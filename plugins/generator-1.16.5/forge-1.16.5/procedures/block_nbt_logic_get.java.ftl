<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
public boolean getValue(IWorld world, BlockPos pos, String tag) {
		TileEntity tileEntity=world.getTileEntity(pos);
		if(tileEntity!=null)
			return tileEntity.getTileData().getBoolean(tag);
		return false;
	}
}.getValue(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$tagName}))
<#-- @formatter:on -->