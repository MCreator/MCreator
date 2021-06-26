(new Object() {
	public int getSpawnZ(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				retval = _bp.getZ();
		}
		return _world.getSpawnPoint().getZ();
	}
}.getSpawnZ(world, ${input$entity}))