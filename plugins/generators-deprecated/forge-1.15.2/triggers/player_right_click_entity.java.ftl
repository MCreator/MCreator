@SubscribeEvent public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
	Entity entity=event.getTarget();
	PlayerEntity sourceentity=event.getPlayer();

	if (event.getHand() != sourceentity.getActiveHand())
		return;

	int i=event.getPos().getX();
	int j=event.getPos().getY();
	int k=event.getPos().getZ();
	World world=event.getWorld();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x", i);
	dependencies.put("y", j);
	dependencies.put("z", k);
	dependencies.put("world" ,world);
	dependencies.put("entity" ,entity);
	dependencies.put("sourceentity" ,sourceentity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}