(new Object(){
public String getValue(IWorld world, BlockPos pos, String tag){
		TileEntity tileEntity=world.getTileEntity(pos);
if(tileEntity!=null) return tileEntity.getTileData().getString(tag);
return "";
}
}.getValue(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${input$tagName}))