(new Object() {
	public int getSpawnY(World _world, Entity _ent) {
		int retval = _world.getSpawnPoint().getY();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getY();
			else
				return retval;
		} else {
			return retval;
	}
}.getSpawnY(world, ${input$entity}))