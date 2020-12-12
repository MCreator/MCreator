@SubscribeEvent public void onBlockPlace(BlockEvent.EntityPlaceEvent event){
	Entity entity = event.getEntity();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",event.getPos().getX());
	dependencies.put("y",event.getPos().getY());
	dependencies.put("z",event.getPos().getZ());
	dependencies.put("px",entity.getPosX());
	dependencies.put("py",entity.getPosY());
	dependencies.put("pz",entity.getPosZ());
	dependencies.put("world",event.getWorld().getWorld());
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}