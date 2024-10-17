@SubscribeEvent public void onPlayerTick(TickEvent.PlayerTickEvent event){
	if(event.phase == TickEvent.Phase.END){
		Entity entity = event.player;
		World world = entity.world;
		int i=(int)entity.posX;
		int j=(int)entity.posY;
		int k=(int)entity.posZ;
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
	}
}