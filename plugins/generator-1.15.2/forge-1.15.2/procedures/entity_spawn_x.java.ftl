(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		int retval = _world.getSpawnPoint().getX();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				retval = _bp.getX();
		}
		return retval;
	}
}.getSpawnX(world, ${input$entity}))
