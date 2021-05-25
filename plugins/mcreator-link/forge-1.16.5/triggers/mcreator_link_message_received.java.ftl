@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onMCreatorLinkMessageReceived(LinkCustomMessageReceivedEvent event){
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("message",new String(event.getData()));
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}