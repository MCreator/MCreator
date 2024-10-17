@SubscribeEvent public void onPlayerFishItem(ItemFishedEvent event){
	PlayerEntity entity=event.getPlayer();
	double i=entity.posX;
	double j=entity.posY;
	double k=entity.posZ;
	World world=entity.world;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}