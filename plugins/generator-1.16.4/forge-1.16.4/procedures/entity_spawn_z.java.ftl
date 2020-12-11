(new Object() {
	public int getSpawnZ(World _world, Entity _ent) {
		int retval = _world.getWorldInfo().getSpawnZ();
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				return _bp.get().getZ();
			else
				return retval;
		} else {
			return retval;
		}
	}
}.getSpawnZ((World) world, ${input$entity}))