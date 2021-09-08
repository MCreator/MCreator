@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBucketFill(FillBucketEvent event) {
		Player entity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", entity.getX());
		dependencies.put("y", entity.getY());
		dependencies.put("z", entity.getZ());
		dependencies.put("world",event.getWorld());
		dependencies.put("itemstack",event.getFilledBucket());
		dependencies.put("originalitemstack",event.getEmptyBucket());
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		execute(dependencies);
	}