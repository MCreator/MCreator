@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onVillageSiege(VillageSiegeEvent event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getAttemptedSpawnPos().x);
		dependencies.put("y", event.getAttemptedSpawnPos().y);
		dependencies.put("z", event.getAttemptedSpawnPos().z);
		dependencies.put("world", event.getWorld());
		dependencies.put("entity", event.getPlayer());
		dependencies.put("event", event);
		execute(dependencies);
	}