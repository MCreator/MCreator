@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Player entity=event.getPlayer();
		if (event.getHand() != entity.getUsedItemHand()) {
			return;
		}
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getPos().getX());
		dependencies.put("y", event.getPos().getY());
		dependencies.put("z", event.getPos().getZ());
		dependencies.put("world", event.getWorld());
		dependencies.put("entity", entity);
		dependencies.put("direction", event.getFace());
		dependencies.put("blockstate", event.getWorld().getBlockState(event.getPos()));
		dependencies.put("event", event);
		execute(dependencies);
	}