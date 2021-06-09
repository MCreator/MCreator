@SubscribeEvent public void onPickup(EntityItemPickupEvent event) {
	PlayerEntity entity=event.getPlayer();
	ItemStack itemstack=event.getItem().getItem();
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	World world=entity.world;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("itemstack",itemstack);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}