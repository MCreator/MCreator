@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onEntityHealed(LivingHealEvent event) {
		Entity entity = event.getEntity();
		double i = entity.getPosX();
		double j = entity.getPosY();
		double k = entity.getPosZ();
		double amount = event.getAmount();
		World world = entity.world;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("amount", amount);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		executeProcedure(dependencies);
	}
}