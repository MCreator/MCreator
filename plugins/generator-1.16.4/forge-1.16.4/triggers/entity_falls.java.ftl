@SubscribeEvent public void onEntityFall(LivingFallEvent event) {
	if (event != null && event.getEntity() != null) {
		Entity entity = event.getEntity();
		double i = entity.getPosX();
       	double j = entity.getPosY();
       	double k = entity.getPosZ();
		double damagemultiplier = event.getDamageMultiplier();
    	double distance = event.getDistance();
		World world = entity.world;
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("damagemultiplier", damagemultiplier);
    	dependencies.put("distance", distance);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}
}