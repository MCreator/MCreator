@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPlayerCriticalHit(CriticalHitEvent event) {
		Player sourceentity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", sourceentity.getX());
		dependencies.put("y", sourceentity.getY());
		dependencies.put("z", sourceentity.getZ());
		dependencies.put("world", sourceentity.level);
		dependencies.put("entity", event.getTarget());
		dependencies.put("sourceentity", sourceentity);
		dependencies.put("damagemodifier", event.getDamageModifier());
		dependencies.put("isvanillacritical", event.isVanillaCritical());
		dependencies.put("event", event);
		execute(dependencies);
	}