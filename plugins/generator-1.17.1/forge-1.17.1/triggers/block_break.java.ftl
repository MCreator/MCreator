@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
		Entity entity = event.getPlayer();
		LevelAccessor world = event.getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("xpAmount",event.getExpToDrop());
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("px",entity.getBlockX());
		dependencies.put("py",entity.getBlockY());
		dependencies.put("pz",entity.getBlockZ());
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("blockstate",event.getState());
		dependencies.put("event",event);
		execute(dependencies);
	}
