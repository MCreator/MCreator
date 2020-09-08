@EventHandler public void onPlayerRespawned(PlayerRespawnEvent event) {
	World world=event.getPlayer().getWorld();
	Entity entity=event.getPlayer();
	double i=entity.getLocation().getX();
	double j=entity.getLocation().getY();
	double k=entity.getLocation().getZ();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}
