@SubscribeEvent public void onBlockPlace(BlockEvent.EntityPlaceEvent event){
	Entity entity = event.getEntity();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",(int) event.getPos().getX());
	dependencies.put("y",(int) event.getPos().getY());
	dependencies.put("z",(int) event.getPos().getZ());
	dependencies.put("px",entity.posX);
	dependencies.put("py",entity.posY);
	dependencies.put("pz",entity.posZ);
	dependencies.put("world",event.getWorld().getWorld());
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}