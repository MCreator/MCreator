@SubscribeEvent public void onAdvancement(AdvancementEvent event) {
	PlayerEntity entity=event.getPlayer();
	double i=entity.posX;
	double j=entity.posY;
	double k=entity.posZ;
	Advancement advancement=event.getAdvancement();
	World world=entity.world;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("advancement",advancement);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}