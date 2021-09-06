@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityGrief(EntityMobGriefingEvent event) {
		Entity entity=event.getEntity();
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
		dependencies.put("event",event);
		execute(dependencies);
	}