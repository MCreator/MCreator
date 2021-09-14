@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityTamed(AnimalTameEvent event) {
		Entity entity = event.getAnimal();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("world",entity.level);
		dependencies.put("entity", entity);
		dependencies.put("sourceentity", event.getTamer());
		dependencies.put("event", event);
		execute(dependencies);
	}