@SubscribeEvent public static void onPlayerFishItem(ItemFishedEvent event) {
	PlayerEntity entity=event.getPlayer();
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	World world=entity.world;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	executeProcedure(dependencies);
}