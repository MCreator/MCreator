@EventHandler public void onBlockBreak(BlockBreakEvent event) {
	World world=event.getPlayer().getWorld();
	Entity entity=event.getPlayer();
	double i=event.getBlock().getLocation().getX();
	double j=event.getBlock().getLocation().getY();
	double k=event.getBlock().getLocation().getZ();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}