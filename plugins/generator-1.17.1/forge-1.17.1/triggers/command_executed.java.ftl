@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onCommand(CommandEvent event) {
		Entity entity = event.getParseResults().getContext().getSource().getEntity();
		if (entity != null) {
			Map<String, Object> dependencies = new HashMap<>();
		    dependencies.put("x", entity.getX());
		    dependencies.put("y", entity.getY());
		    dependencies.put("z", entity.getZ());
			dependencies.put("world",entity.level);
			dependencies.put("entity",entity);
			dependencies.put("command",event.getParseResults().getReader().getString());
			dependencies.put("event",event);
			execute(dependencies);
		}
	}