if(!world.isRemote() && world instanceof World) {
	Entity entityToSpawn = ${input$projectile};
	entityToSpawn.setPosition(${input$x}, ${input$y}, ${input$z});
	if (entityToSpawn instanceof IProjectile)
	    ((IProjectile) entityToSpawn).shoot(${input$dx}, ${input$dy}, ${input$dz}, (float) ${input$speed}, (float) ${input$inaccuracy});
	world.addEntity(entityToSpawn);
}