if(world instanceof World && !world.getWorld().isRemote) {
	world.getWorld().createExplosion(null,(int)${input$x},(int)${input$y},(int)${input$z},(float)${input$power}, Explosion.Mode.${field$mode});
}
