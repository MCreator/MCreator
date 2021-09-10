@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onCropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("blockstate",event.getState());
		dependencies.put("world",event.getWorld());
		dependencies.put("event",event);
		execute(dependencies);
	}