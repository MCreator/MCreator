@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBucketFill(FillBucketEvent event) {
		Player entity=event.getPlayer();
		double i=entity.getX();
		double j=entity.getY();
		double k=entity.getZ();
		LevelAccessor world=event.getWorld();
		ItemStack itemstack=event.getFilledBucket();
		ItemStack originalitemstack=event.getEmptyBucket();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("itemstack",itemstack);
		dependencies.put("originalitemstack",originalitemstack);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		execute(dependencies);
	}