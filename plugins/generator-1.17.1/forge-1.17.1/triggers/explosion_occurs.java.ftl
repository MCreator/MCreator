@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onExplode(ExplosionEvent.Detonate event) {
		Explosion explosion = event.getExplosion();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", explosion.getPosition().x);
		dependencies.put("y", explosion.getPosition().y);
		dependencies.put("z", explosion.getPosition().z);
		dependencies.put("world", event.getWorld());
		dependencies.put("event",event);
		execute(dependencies);
	}