(new Object() {
	public int getSpawnZ(World _world, Entity _ent) {
		int retval = _world.getWorldInfo().getSpawnZ();
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				retval = _bp.get().getZ();
		}
		return retval;
	}
}.getSpawnZ((World) world, ${input$entity}))