if(!world.isRemote() && world instanceof World) {
	World projectileLevel = (World) world;
	ProjectileEntity entityToSpawn = ${input$projectile};
	entityToSpawn.setPosition(${input$x}, ${input$y}, ${input$z});
	entityToSpawn.shoot(${input$dx}, ${input$dy}, ${input$dz}, ${opt.toFloat(input$speed)}, ${opt.toFloat(input$inaccuracy)});
	world.addEntity(entityToSpawn);
}