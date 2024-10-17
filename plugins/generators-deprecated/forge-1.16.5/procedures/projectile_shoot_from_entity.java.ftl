{
	Entity _shootFrom = ${input$entity};
	World projectileLevel = _shootFrom.world;
	if (!projectileLevel.isRemote()) {
		ProjectileEntity _entityToSpawn = ${input$projectile};
		_entityToSpawn.setPosition(_shootFrom.getPosX(), _shootFrom.getPosYEye() - 0.1, _shootFrom.getPosZ());
		_entityToSpawn.shoot(_shootFrom.getLookVec().x, _shootFrom.getLookVec().y, _shootFrom.getLookVec().z, ${opt.toFloat(input$speed)}, ${opt.toFloat(input$inaccuracy)});
		projectileLevel.addEntity(_entityToSpawn);
	}
}