@SubscribeEvent public void onChat(ServerChatEvent event){
	ServerPlayerEntity entity=event.getPlayer();
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",entity.world);
	dependencies.put("entity",entity);
	dependencies.put("text",event.getMessage());
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}