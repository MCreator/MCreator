{
	Entity _shootFrom = ${input$entity};
	Level projectileLevel = _shootFrom.level();
	if (!projectileLevel.isClientSide()) {
		Projectile _entityToSpawn = ${input$projectile};
		_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
		_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, ${opt.toFloat(input$speed)}, ${opt.toFloat(input$inaccuracy)});
		projectileLevel.addFreshEntity(_entityToSpawn);
	}
}