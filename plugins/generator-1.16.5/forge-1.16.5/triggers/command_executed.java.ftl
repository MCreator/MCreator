@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onCommand(CommandEvent event) {
		Entity entity = event.getParseResults().getContext().getSource().getEntity();
		if (entity != null) {
			double i = entity.getPosX();
			double j = entity.getPosY();
			double k = entity.getPosZ();
			HashMap<String, String> command = new HashMap<>();
            int index = -1;
            for (String param : event.getParseResults().getReader().getString().split("\\s+")) {
            	if (index >= 0)
            		command.put(Integer.toString(index), param);
            	index++;
            }
		    CommandContext<CommandSource> ctx = event.getParseResults().getContext().build(event.getParseResults().getReader().getString());
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("x",i);
			dependencies.put("y",j);
			dependencies.put("z",k);
			dependencies.put("world",entity.world);
			dependencies.put("entity",entity);
			dependencies.put("command",command);
		    dependencies.put("cmdcontext", ctx);
			dependencies.put("event",event);
			executeProcedure(dependencies);
		}
	}
}