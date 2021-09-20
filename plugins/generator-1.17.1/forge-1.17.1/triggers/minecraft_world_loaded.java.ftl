@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("world",event.getWorld());
		dependencies.put("event",event);
		execute(dependencies);
	}