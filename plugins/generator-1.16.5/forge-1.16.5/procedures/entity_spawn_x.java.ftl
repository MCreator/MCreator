(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity)
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null)
				return ((ServerPlayerEntity) _ent).func_241140_K_().getX();
		else if (_world.getWorldBorder().contains(new BlockPos(_world.getWorldInfo().getSpawnX(), _world.getWorldInfo().getSpawnY(), _world.getWorldInfo().getSpawnZ())))
			return _world.getWorldInfo().getSpawnX();
		return (int) _world.getWorldBorder().getCenterX();
	}
}.getSpawnX(world, ${input$entity}))