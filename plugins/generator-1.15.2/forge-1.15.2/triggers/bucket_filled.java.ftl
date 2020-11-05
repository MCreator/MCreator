@SubscribeEvent public void onBucketFill(FillBucketEvent event){
	PlayerEntity entity=event.getPlayer();
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	World world=event.getWorld();
	ItemStack itemstack=event.getFilledBucket();
	ItemStack bucket=event.getEmptyBucket();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("itemstack",itemstack);
	dependencies.put("bucket",bucket);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}
