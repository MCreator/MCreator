if (world instanceof ServerLevel projectileLevel) {
	Projectile _entityToSpawn = ${input$projectile};
	_entityToSpawn.setPos(${input$x}, ${input$y}, ${input$z});
	_entityToSpawn.shoot(${input$dx}, ${input$dy}, ${input$dz}, ${opt.toFloat(input$speed)}, ${opt.toFloat(input$inaccuracy)});
	projectileLevel.addFreshEntity(_entityToSpawn);
}