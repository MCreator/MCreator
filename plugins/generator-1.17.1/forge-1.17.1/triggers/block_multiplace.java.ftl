@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
		Entity entity = event.getEntity();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("px",entity.getX());
		dependencies.put("py",entity.getY());
		dependencies.put("pz",entity.getZ());
		dependencies.put("world",event.getWorld());
		dependencies.put("entity",entity);
		dependencies.put("blockstate",event.getState());
		dependencies.put("placedagainst",event.getPlacedAgainst());
		dependencies.put("event",event);
		execute(dependencies);
	}
