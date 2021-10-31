@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("world",event.getWorld());
		dependencies.put("event",event);
		execute(dependencies);
	}