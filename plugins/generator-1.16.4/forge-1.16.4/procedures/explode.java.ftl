if(world instanceof World && !world.isRemote()) {
	((World) world).createExplosion(null,(int)${input$x},(int)${input$y},(int)${input$z},(float)${input$power}, Explosion.Mode.BREAK);
}