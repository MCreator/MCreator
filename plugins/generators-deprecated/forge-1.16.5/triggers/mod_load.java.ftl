@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) private static class GlobalTrigger {
	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		executeProcedure(Collections.emptyMap());
	}
}