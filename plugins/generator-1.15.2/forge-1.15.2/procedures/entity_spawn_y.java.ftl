(new Object() {
	public int getSpawnY(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getY();
		}
		return _world.getSpawnPoint().getY();
	}
}.getSpawnY(world, ${input$entity}))