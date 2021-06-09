@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onBucketFill(FillBucketEvent event) {
		PlayerEntity entity=event.getPlayer();
		double i=entity.getPosX();
		double j=entity.getPosY();
		double k=entity.getPosZ();
		World world=event.getWorld();
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
		executeProcedure(dependencies);
	}
}