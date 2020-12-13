if (world instanceof World && !((World) world).getWorld().isRemote) {
	((World) world).getWorld().addEntity(new ExperienceOrbEntity(((World) world).getWorld(), ${input$x}, ${input$y}, ${input$z},(int)${input$xpamount}));
}