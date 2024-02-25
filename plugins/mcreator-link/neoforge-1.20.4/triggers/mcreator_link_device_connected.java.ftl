@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onLinkDeviceConnected(LinkDeviceConnectedEvent event){
		execute(event);
	}