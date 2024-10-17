if(entity instanceof EntityPlayer&&!world.isRemote){
	((EntityPlayer)entity).sendStatusMessage(new TextComponentString(${input$text}), ${input$actbar});
}