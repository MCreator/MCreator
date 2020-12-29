(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		int retval = _world.getWorldInfo().getSpawnX();
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				retval = _bp.get().getX();
		}
		return retval;
	}
}.getSpawnX(world, ${input$entity}))