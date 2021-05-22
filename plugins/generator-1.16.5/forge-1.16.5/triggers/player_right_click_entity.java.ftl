@SubscribeEvent public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
	Entity entity=event.getTarget();
	PlayerEntity sourceentity=event.getPlayer();
	if (event.getHand() != sourceentity.getActiveHand()) {
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
	dependencies.put("world" ,world);
	dependencies.put("entity" ,entity);
	dependencies.put("sourceentity" ,sourceentity);
	dependencies.put("event",event);
	executeProcedure(dependencies);
}