(new Object() {
	public int getSpawnZ(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity)
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null)
				return ((ServerPlayerEntity) _ent).func_241140_K_().getZ();
		else if (_world.getWorldBorder().contains(new BlockPos(_world.getWorldInfo().getSpawnX(), _world.getWorldInfo().getSpawnY(), _world.getWorldInfo().getSpawnZ())))
			return _world.getWorldInfo().getSpawnZ();
		return (int) _world.getWorldBorder().getCenterZ();
	}
}.getSpawnZ(world, ${input$entity}))