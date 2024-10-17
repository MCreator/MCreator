if(!world.isRemote){
	world.spawnEntity(new EntityXPOrb(world, ${input$x}, ${input$y}, ${input$z},(int)${input$xpamount}));
}