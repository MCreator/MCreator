(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = ((PlayerEntity) _ent).getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getX();
		}
		return _world.getSpawnPoint().getX();
	}
}.getSpawnX(world, ${input$entity}))