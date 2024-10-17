if(world instanceof World && !world.getWorld().isRemote) {
	world.getWorld().addEntity(new ExperienceOrbEntity(world.getWorld(), ${input$x}, ${input$y}, ${input$z},(int)${input$xpamount}));
}