@SubscribeEvent public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
	PlayerEntity entity=event.getPlayer();
	if (event.getHand() != entity.getActiveHand())
		return;

	int i=event.getPos().getX();
	int j=event.getPos().getY();
	int k=event.getPos().getZ();
	World world=event.getWorld();
	BlockState state = world.getBlockState(event.getPos());
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x", i);
	dependencies.put("y", j);
	dependencies.put("z", k);
	dependencies.put("world", world);
	dependencies.put("entity", entity);
	dependencies.put("direction", event.getFace());
	dependencies.put("blockstate", state);
	dependencies.put("event", event);
	this.executeProcedure(dependencies);
}