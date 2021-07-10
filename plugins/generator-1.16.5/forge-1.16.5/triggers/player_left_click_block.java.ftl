@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		PlayerEntity entity=event.getPlayer();
		if (event.getHand() != entity.getActiveHand()) {
			return;
		}
		double i=event.getPos().getX();
		double j=event.getPos().getY();
		double k=event.getPos().getZ();
		IWorld world=event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("direction", event.getFace());
		dependencies.put("blockstate", state);
		dependencies.put("event", event);
		executeProcedure(dependencies);
	}
}