@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event) {
		Entity entity = event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("world", entity.level);
		dependencies.put("entity",entity);
		dependencies.put("endconquered",event.isEndConquered());
		dependencies.put("event",event);
		execute(dependencies);
	}