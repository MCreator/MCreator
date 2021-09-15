@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onGemDropped(ItemTossEvent event) {
		Player entity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("entity", entity);
		dependencies.put("itemstack", event.getEntityItem().getItem());
		dependencies.put("event",event);
		execute(dependencies);
	}