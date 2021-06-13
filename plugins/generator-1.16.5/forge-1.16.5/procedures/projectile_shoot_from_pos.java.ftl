if(!world.isRemote() && world instanceof World) {
    World spawnWorld = (World) world;
	${input$projectile}
	entityToSpawn.setPosition(${input$x}, ${input$y}, ${input$z});
	entityToSpawn.shoot(${input$dx}, ${input$dy}, ${input$dz}, (float) ${input$speed}, (float) ${input$inaccuracy});
	spawnWorld.addEntity(entityToSpawn);
}