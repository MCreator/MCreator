@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBonemeal(BonemealEvent event){
		Player entity=event.getPlayer();
		double i=event.getPos().getX();
		double j=event.getPos().getY();
		double k=event.getPos().getZ();
		Level world=event.getWorld();
		ItemStack itemstack=event.getStack();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("itemstack",itemstack);
		dependencies.put("entity",entity);
		dependencies.put("blockstate",event.getBlock());
		dependencies.put("event",event);
		execute(dependencies);
	}