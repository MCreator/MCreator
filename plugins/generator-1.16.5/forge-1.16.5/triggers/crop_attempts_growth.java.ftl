@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onCropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		IWorld world = event.getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("world",world);
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}