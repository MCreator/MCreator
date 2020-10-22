(new Object() {
	public int getSpawnY(Entity _ent) {
		int retval = world.getSpawnPoint().getY();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(world.getDimension().getType());
			if (_bp != null)
				return _bp.getY();
			else
				return retval;
		} else {
			return retval;
	}
}.getSpawnY(${input$entity}))