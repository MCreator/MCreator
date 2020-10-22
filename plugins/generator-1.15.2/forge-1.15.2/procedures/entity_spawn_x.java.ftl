(new Object() {
	public int getSpawnX(Entity _ent) {
		int retval = world.getSpawnPoint().getX();
		if (_ent instanceof PlayerEntity) {
			BlockPos _bp = _ent.getBedLocation(world.getDimension().getType());
			if (_bp != null)
				return _bp.getX();
			else
				return retval;
		} else {
			return retval;
	}
}.getSpawnX(${input$entity}))