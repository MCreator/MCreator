@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase==TickEvent.Phase.END) {
			IWorld world=event.world;
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("world",world);
			dependencies.put("event",event);
			executeProcedure(dependencies);
		}
	}
}