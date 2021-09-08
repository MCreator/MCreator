@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityDeath(LivingDeathEvent event) {
		if (event!=null && event.getEntity()!=null) {
			Entity entity=event.getEntity();
			Entity sourceentity=event.getSource().getEntity();
			LevelAccessor world=entity.level;
			Map<String, Object> dependencies = new HashMap<>();
		    dependencies.put("x", entity.getX());
		    dependencies.put("y", entity.getY());
		    dependencies.put("z", entity.getZ());
			dependencies.put("world",world);
			dependencies.put("entity",entity);
			dependencies.put("sourceentity",sourceentity);
			dependencies.put("event",event);
			execute(dependencies);
		}
	}