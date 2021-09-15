@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onItemExpire(ItemExpireEvent event) {
		Entity entity=event.getEntity();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		dependencies.put("itemstack",event.getEntityItem().getItem());
		execute(dependencies);
	}