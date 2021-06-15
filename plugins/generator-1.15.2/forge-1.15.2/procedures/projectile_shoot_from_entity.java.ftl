if(${input$entity} != null) {
    Entity _shootFrom = ${input$entity};
    if (!_shootFrom.world.isRemote()) {
        World spawnWorld = _shootFrom.world;
		${input$projectile}
		entityToSpawn.setPosition(_shootFrom.getPosX(), _shootFrom.getPosYEye() - 0.1d, _shootFrom.getPosZ());
		if (entityToSpawn instanceof IProjectile)
		    ((IProjectile) entityToSpawn).shoot(_shootFrom.getLookVec().x, _shootFrom.getLookVec().y, _shootFrom.getLookVec().z, (float) ${input$speed}, (float) ${input$inaccuracy});
		spawnWorld.addEntity(entityToSpawn);
	}
}