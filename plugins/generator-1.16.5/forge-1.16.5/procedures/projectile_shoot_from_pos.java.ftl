if(!world.isRemote() && world instanceof World) {
	ProjectileEntity entityToSpawn = ${input$projectile};
	entityToSpawn.setPosition(${input$x}, ${input$y}, ${input$z});
	entityToSpawn.shoot(${input$dx}, ${input$dy}, ${input$dz}, (float) ${input$speed}, (float) ${input$inaccuracy});
	world.addEntity(entityToSpawn);
}