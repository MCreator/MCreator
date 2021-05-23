@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onVillageSiege(VillageSiegeEvent event) {
		PlayerEntity entity=event.getPlayer();
		double i=event.getAttemptedSpawnPos().x;
		double j=event.getAttemptedSpawnPos().y;
		double k=event.getAttemptedSpawnPos().z;
		IWorld world=event.getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		executeProcedure(dependencies);
	}
}