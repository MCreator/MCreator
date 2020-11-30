<#-- @formatter:off -->
(new Object(){
public boolean getValue(BlockPos pos, String tag) {
		TileEntity tileEntity=world.getTileEntity(pos);
		if(tileEntity!=null)
			return tileEntity.getTileData().getBoolean(tag);
		return false;
	}
}.getValue(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${input$tagName}))
<#-- @formatter:on -->