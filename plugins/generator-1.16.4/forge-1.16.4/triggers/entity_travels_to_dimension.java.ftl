@SubscribeEvent public void onEntityTravelToDimension(EntityTravelToDimensionEvent event){
	Entity entity=event.getEntity();
	World world = entity.world;
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("dimension",event.getDimension());
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}