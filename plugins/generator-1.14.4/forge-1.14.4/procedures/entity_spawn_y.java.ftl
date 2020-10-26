(new Object() {
	public int getSpawnY(IWorld _world, LivingEntity _ent) {
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
	}
}.getSpawnY(world, (LivingEntity) ${input$entity}))