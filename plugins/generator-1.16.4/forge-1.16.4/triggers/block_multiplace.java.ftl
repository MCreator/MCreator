@SubscribeEvent public void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
	Entity entity = event.getEntity();
	IWorld world = event.getWorld();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",event.getPos().getX());
	dependencies.put("y",event.getPos().getY());
	dependencies.put("z",event.getPos().getZ());
	dependencies.put("px",entity.getPosX());
	dependencies.put("py",entity.getPosY());
	dependencies.put("pz",entity.getPosZ());
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}