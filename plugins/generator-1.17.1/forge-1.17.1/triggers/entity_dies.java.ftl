@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityDeath(LivingDeathEvent event) {
		if (event!=null && event.getEntity()!=null) {
			Entity entity=event.getEntity();
			Entity sourceentity=event.getSource().getEntity();
			double i=entity.getX();
			double j=entity.getY();
			double k=entity.getZ();
			LevelAccessor world=entity.level;
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("x",i);
			dependencies.put("y",j);
			dependencies.put("z",k);
			dependencies.put("world",world);
			dependencies.put("entity",entity);
			dependencies.put("sourceentity",sourceentity);
			dependencies.put("event",event);
			execute(dependencies);
		}
	}