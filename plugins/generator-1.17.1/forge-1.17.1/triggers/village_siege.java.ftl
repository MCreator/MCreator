@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onVillageSiege(VillageSiegeEvent event) {
		Player entity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getAttemptedSpawnPos().x);
		dependencies.put("y", event.getAttemptedSpawnPos().y);
		dependencies.put("z", event.getAttemptedSpawnPos().z);
		dependencies.put("world", event.getWorld());
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		executeProcedure(dependencies);
	}