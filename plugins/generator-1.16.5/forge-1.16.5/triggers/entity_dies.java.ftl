@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onEntityDeath(LivingDeathEvent event) {
		if (event!=null && event.getEntity()!=null) {
			Entity entity=event.getEntity();
			Entity sourceentity=event.getSource().getTrueSource();
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
			dependencies.put("sourceentity",sourceentity);
			dependencies.put("event",event);
			executeProcedure(dependencies);
		}
	}
}