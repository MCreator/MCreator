@Mod.EventBusSubscriber private static class GlobalTrigger {
	@SubscribeEvent public static void onLinkDeviceConnected(LinkDeviceConnectedEvent event){
		executeProcedure(Collections.emptyMap());
	}
}