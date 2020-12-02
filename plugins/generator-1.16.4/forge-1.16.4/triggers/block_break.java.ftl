@SubscribeEvent public void onBlockBreak(BlockEvent.BreakEvent event) {
	Entity entity = event.getPlayer();
	World world = event.getWorld();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("xpAmount",event.getExpToDrop());
	dependencies.put("x",(int)event.getPos().getX());
	dependencies.put("y",(int)event.getPos().getY());
	dependencies.put("z",(int)event.getPos().getZ());
	dependencies.put("px",entity.getPosX());
	dependencies.put("py",entity.getPosY());
	dependencies.put("pz",entity.getPosZ());
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}