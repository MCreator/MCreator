@SubscribeEvent public void onEntitySpawned(EntityJoinWorldEvent event){
		Entity entity=event.getEntity();
		double i=entity.posX;
		double j=entity.posY;
		double k=entity.posZ;
		World world=event.getWorld().getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
}