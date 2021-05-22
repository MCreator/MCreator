@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onCommand(CommandEvent event) {
		Entity entity = event.getParseResults().getContext().getSource().getEntity();
		if (entity != null) {
			double i = entity.getPosX();
			double j = entity.getPosY();
			double k = entity.getPosZ();
			String command=event.getParseResults().getReader().getString();
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("x",i);
			dependencies.put("y",j);
			dependencies.put("z",k);
			dependencies.put("world",entity.world);
			dependencies.put("entity",entity);
			dependencies.put("command",command);
			dependencies.put("event",event);
			executeProcedure(dependencies);
		}
	}
}