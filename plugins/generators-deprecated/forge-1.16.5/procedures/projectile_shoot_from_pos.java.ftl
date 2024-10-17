if(world instanceof ServerWorld) {
	World projectileLevel = (World) world;
	ProjectileEntity _entityToSpawn = ${input$projectile};
	_entityToSpawn.setPosition(${input$x}, ${input$y}, ${input$z});
	_entityToSpawn.shoot(${input$dx}, ${input$dy}, ${input$dz}, ${opt.toFloat(input$speed)}, ${opt.toFloat(input$inaccuracy)});
	world.addEntity(_entityToSpawn);
}