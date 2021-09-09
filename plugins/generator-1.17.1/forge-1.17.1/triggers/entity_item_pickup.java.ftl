@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPickup(EntityItemPickupEvent event) {
	    Player entity = event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("world",entity.level);
		dependencies.put("entity",entity);
		dependencies.put("itemstack",event.getItem().getItem());
		dependencies.put("event",event);
		execute(dependencies);
	}