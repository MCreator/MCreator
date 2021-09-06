@Mod.EventBusSubscriber public class GlobalTrigger {
	@SubscribeEvent public static void onChat(ServerChatEvent event){
		ServerPlayer entity=event.getPlayer();
		double i=entity.getX();
		double j=entity.getY();
		double k=entity.getZ();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",entity.level);
		dependencies.put("entity",entity);
		dependencies.put("text",event.getMessage());
		dependencies.put("event",event);
		execute(dependencies);
	}
}