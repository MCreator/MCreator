@SubscribeEvent public static void onBonemeal(BonemealEvent event){
	PlayerEntity entity=event.getPlayer();
	double i=event.getPos().getX();
	double j=event.getPos().getY();
	double k=event.getPos().getZ();
	World world=event.getWorld();
	ItemStack itemstack=event.getStack();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("itemstack",itemstack);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	executeProcedure(dependencies);
}