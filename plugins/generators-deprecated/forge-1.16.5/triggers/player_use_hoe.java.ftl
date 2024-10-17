@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onUseHoe(UseHoeEvent event) {
		PlayerEntity entity=event.getPlayer();
		double i=event.getContext().getPos().getX();
		double j=event.getContext().getPos().getY();
		double k=event.getContext().getPos().getZ();
		World world=entity.world;
		BlockState state = world.getBlockState(event.getContext().getPos());
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("blockstate",state);
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}