@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Player sourceentity=event.getPlayer();
		if (event.getHand() != sourceentity.getUsedItemHand()) {
			return;
		}
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getPos().getX());
		dependencies.put("y", event.getPos().getY());
		dependencies.put("z", event.getPos().getZ());
		dependencies.put("world", event.getWorld());
		dependencies.put("entity", event.getTarget());
		dependencies.put("sourceentity", sourceentity);
		dependencies.put("event", event);
		execute(dependencies);
	}