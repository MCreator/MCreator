@Mod.EventBusSubscriber public  class GlobalTrigger {
	@SubscribeEvent public static void onCommand(CommandEvent event) {
		Entity entity = event.getParseResults().getContext().getSource().getEntity();
		if (entity != null) {
			double i = entity.getX();
			double j = entity.getY();
			double k = entity.getZ();
			String command=event.getParseResults().getReader().getString();
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("x",i);
			dependencies.put("y",j);
			dependencies.put("z",k);
			dependencies.put("world",entity.level);
			dependencies.put("entity",entity);
			dependencies.put("command",command);
			dependencies.put("event",event);
			execute(dependencies);
		}
	}
}