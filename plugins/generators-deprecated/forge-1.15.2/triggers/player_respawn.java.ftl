@SubscribeEvent public void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event){
	Entity entity = event.getPlayer();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",entity.getPosX());
	dependencies.put("y",entity.getPosY());
	dependencies.put("z",entity.getPosZ());
	dependencies.put("world",entity.world);
	dependencies.put("entity",entity);
	dependencies.put("endconquered",event.isEndConquered());
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}