if(world instanceof World && !((World) world).isRemote) {
	((World) world).createExplosion(null,(int)${input$x},(int)${input$y},(int)${input$z},(float)${input$power}, Explosion.Mode.${field$mode!"BREAK"});
}