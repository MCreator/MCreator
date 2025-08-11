@EventBusSubscriber(Dist.DEDICATED_SERVER) public class ${name}Procedure {
	@SubscribeEvent public static void init(FMLDedicatedServerSetupEvent event) {
		execute();
	}