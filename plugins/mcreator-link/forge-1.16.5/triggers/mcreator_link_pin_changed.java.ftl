@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onMCreatorLinkPinChanged(LinkDigitalPinChangedEvent event){
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("pin",event.getPin());
		dependencies.put("value",(int)event.getValue());
		dependencies.put("risingEdge",event.isRisingEdge());
		dependencies.put("event",event);
		executeProcedure(dependencies);
	}
}