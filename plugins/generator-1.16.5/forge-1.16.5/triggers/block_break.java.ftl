@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
		Entity entity = event.getPlayer();
		IWorld world = event.getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("xpAmount",event.getExpToDrop());
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("px",entity.getPosX());
		dependencies.put("py",entity.getPosY());
		dependencies.put("pz",entity.getPosZ());
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}