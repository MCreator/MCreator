(new Object() {
	public int getSpawnZ(Entity _ent) {
		int retval = world.getSpawnPoint().getZ();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(world.getDimension().getType());
			if (_bp != null)
				return _bp.getZ();
			else
				return retval;
		} else {
			return retval;
	}
}.getSpawnZ(${input$entity}))