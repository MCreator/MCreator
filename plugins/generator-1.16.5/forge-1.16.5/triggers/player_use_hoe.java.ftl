@SubscribeEvent public void onUseHoe(UseHoeEvent event) {
	PlayerEntity entity=event.getPlayer();
	double i=event.getContext().getPos().getX();
	double j=event.getContext().getPos().getY();
	double k=event.getContext().getPos().getZ();
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