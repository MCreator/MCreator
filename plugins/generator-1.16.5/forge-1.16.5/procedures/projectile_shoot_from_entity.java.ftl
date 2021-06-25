if(${input$entity} != null) {
    Entity _shootFrom = ${input$entity};
    if (!_shootFrom.world.isRemote()) {
        IWorld _orig = world;
        world = _shootFrom.world;
		ProjectileEntity entityToSpawn = ${input$projectile};
		entityToSpawn.setPosition(_shootFrom.getPosX(), _shootFrom.getPosYEye() - 0.1d, _shootFrom.getPosZ());
		entityToSpawn.shoot(_shootFrom.getLookVec().x, _shootFrom.getLookVec().y, _shootFrom.getLookVec().z, (float) ${input$speed}, (float) ${input$inaccuracy});
		world.addEntity(entityToSpawn);
		world = _orig;
	}
}