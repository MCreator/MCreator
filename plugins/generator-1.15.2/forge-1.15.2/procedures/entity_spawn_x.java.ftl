(new Object() {
	public int getSpawnX(World _world, Entity _ent) {
		int retval = _world.getSpawnPoint().getX();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getX();
			else
				return retval;
		} else {
			return retval;
		}
	}
}.getSpawnX(world, ${input$entity}))