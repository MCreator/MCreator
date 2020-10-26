(new Object() {
	public int getSpawnZ(IWorld _world, LivingEntity _ent) {
		int retval = _world.getSpawnPoint().getZ();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(_world.getDimension().getType());
			if (_bp != null)
				return _bp.getZ();
			else
				return retval;
		} else {
			return retval;
		}
	}
}.getSpawnZ(world, (LivingEntity) ${input$entity}))