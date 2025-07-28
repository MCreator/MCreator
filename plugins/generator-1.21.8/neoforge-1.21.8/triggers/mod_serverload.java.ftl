@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void init(FMLDedicatedServerSetupEvent event) {
		execute();
	}