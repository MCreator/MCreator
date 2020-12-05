@SubscribeEvent public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
	PlayerEntity entity=event.getPlayer();
	if (event.getHand() != entity.getActiveHand()) {
		return;
	}
	double i=event.getPos().getX();
	double j=event.getPos().getY();
	double k=event.getPos().getZ();
	IWorld world=event.getWorld();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x", i);
	dependencies.put("y", j);
	dependencies.put("z", k);
	dependencies.put("world", world);
	dependencies.put("entity", entity);
	dependencies.put("event", event);
	this.executeProcedure(dependencies);
}