@SubscribeEvent public static void onPlayerXPLevelChange(PlayerXpEvent.LevelChange event) {
	if (event != null && event.getEntity() != null) {
		Entity entity = event.getEntity();
		double i = entity.getPosX();
		double j = entity.getPosY();
		double k = entity.getPosZ();
		int amount = event.getLevels();
		World world = entity.world;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("amount", amount);
		dependencies.put("event", event);
		executeProcedure(dependencies);
	}
}