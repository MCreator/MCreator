if (world instanceof World && !((World) world).isRemote) {
	((World) world).addEntity(new ExperienceOrbEntity(((World) world), ${input$x}, ${input$y}, ${input$z},(int)${input$xpamount}));
}