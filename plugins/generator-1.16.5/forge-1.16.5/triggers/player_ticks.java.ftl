@SubscribeEvent public void onPlayerTick(TickEvent.PlayerTickEvent event) {
	if(event.phase == TickEvent.Phase.END){
		Entity entity = event.player;
		World world = entity.world;
		double i=entity.getPosX();
		double j=entity.getPosY();
		double k=entity.getPosZ();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
	}
}