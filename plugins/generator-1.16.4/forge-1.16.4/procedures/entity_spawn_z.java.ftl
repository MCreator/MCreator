(new Object() {
	public int getSpawnZ(World _world, Entity _ent) {
		int retval = _world.getSpawnPoint().getZ();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getZ();
			else
				return retval;
		} else {
			return retval;
		}
	}
}.getSpawnZ(world, ${input$entity}))