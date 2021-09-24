@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase==TickEvent.Phase.END) {
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("world",event.world);
			dependencies.put("event",event);
			executeProcedure(dependencies);
		}
	}