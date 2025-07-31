@EventBusSubscriber({Dist.CLIENT}) public class ${name}Procedure {
	@SubscribeEvent public static void init(FMLClientSetupEvent event) {
		execute();
	}