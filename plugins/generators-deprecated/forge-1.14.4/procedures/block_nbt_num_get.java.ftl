(new Object(){
public double getValue(BlockPos pos, String tag){
		TileEntity tileEntity=world.getTileEntity(pos);
if(tileEntity!=null) return tileEntity.getTileData().getDouble(tag);
return -1;
}
}.getValue(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${input$tagName}))