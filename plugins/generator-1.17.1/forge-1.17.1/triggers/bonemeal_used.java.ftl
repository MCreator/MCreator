@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBonemeal(BonemealEvent event){
		Player entity=event.getPlayer();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",event.getPos().getX());
		dependencies.put("y",event.getPos().getY());
		dependencies.put("z",event.getPos().getZ());
		dependencies.put("world",event.getWorld());
		dependencies.put("itemstack",event.getStack());
		dependencies.put("entity",entity);
		dependencies.put("blockstate",event.getBlock());
		dependencies.put("event",event);
		execute(dependencies);
	}