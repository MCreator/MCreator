(new Object() {
	public int getSpawnY(World _world, Entity _ent) {
		int retval = _world.getWorldInfo().getSpawnY();
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				return _bp.get().getY();
			else
				return retval;
		} else {
			return retval;
		}
	}
}.getSpawnY((World) world, ${input$entity}))