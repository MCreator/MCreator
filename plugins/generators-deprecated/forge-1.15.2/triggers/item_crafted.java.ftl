@SubscribeEvent public void onItemCrafted(PlayerEvent.ItemCraftedEvent event){
	Entity entity = event.getPlayer();
	World world = entity.world;
	double i=entity.getPosX();
	double j=entity.getPosY();
	double k=entity.getPosZ();
	ItemStack itemStack = event.getCrafting();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("entity",entity);
	dependencies.put("itemstack",itemStack);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}