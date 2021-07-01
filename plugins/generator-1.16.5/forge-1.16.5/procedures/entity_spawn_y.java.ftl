(new Object() {
	public int getSpawnY(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity)
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null)
				return ((ServerPlayerEntity) _ent).func_241140_K_().getY();
		else if (_world.getWorldBorder().contains(new BlockPos(_world.getWorldInfo().getSpawnX(), _world.getWorldInfo().getSpawnY(), _world.getWorldInfo().getSpawnZ())))
			return _world.getWorldInfo().getSpawnY();
		return _world.getHeight(Heightmap.Type.MOTION_BLOCKING, (int) _world.getWorldBorder().getCenterX(), (int) _world.getWorldBorder().getCenterZ());
	}
}.getSpawnY(world, ${input$entity}))