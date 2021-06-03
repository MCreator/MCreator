@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onSaplingGrow(SaplingGrowTreeEvent event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("world",event.getWorld());
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}