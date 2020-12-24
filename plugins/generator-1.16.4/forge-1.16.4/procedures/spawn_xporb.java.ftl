if (world instanceof World && !world.isRemote()) {
	((World) world).addEntity(new ExperienceOrbEntity(((World) world), ${input$x}, ${input$y}, ${input$z},(int)${input$xpamount}));
}