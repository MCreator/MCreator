@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseHoe(UseHoeEvent event) {
		Player entity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getContext().getClickedPos().getX());
		dependencies.put("y", event.getContext().getClickedPos().getY());
		dependencies.put("z", event.getContext().getClickedPos().getZ());
		dependencies.put("world",entity.level);
		dependencies.put("entity",entity);
		dependencies.put("blockstate",entity.level.getBlockState(event.getContext().getClickedPos()));
		dependencies.put("event",event);
		execute(dependencies);
	}