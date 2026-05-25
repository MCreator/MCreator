@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		execute();
	}