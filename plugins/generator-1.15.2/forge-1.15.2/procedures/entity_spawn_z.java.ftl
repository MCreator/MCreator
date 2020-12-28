(new Object() {
	public int getSpawnZ(IWorld _world, Entity _ent) {
		int retval = _world.getSpawnPoint().getZ();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				retval = _bp.getZ();
		}
		return retval;
	}
}.getSpawnZ(world, ${input$entity}))