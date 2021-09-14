@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onFarmlandTrampled(BlockEvent.FarmlandTrampleEvent event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("world",event.getWorld());
		dependencies.put("entity",event.getEntity());
		dependencies.put("blockstate",event.getState());
		dependencies.put("falldistance",event.getFallDistance());
		dependencies.put("event",event);
		execute(dependencies);
	}
