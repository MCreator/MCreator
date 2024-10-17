@SubscribeEvent public void onBlockBreak(BlockEvent.BreakEvent event){
	Entity entity = event.getPlayer();
	java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
	dependencies.put("xpAmount",event.getExpToDrop());
	dependencies.put("x",(int)event.getPos().getX());
	dependencies.put("y",(int)event.getPos().getY());
	dependencies.put("z",(int)event.getPos().getZ());
	dependencies.put("px",(int)entity.posX);
	dependencies.put("py",(int)entity.posY);
	dependencies.put("pz",(int)entity.posZ);
	dependencies.put("world",event.getWorld());
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}