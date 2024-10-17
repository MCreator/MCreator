@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT}) private static class GlobalTrigger {
	@SubscribeEvent public static void init(FMLClientSetupEvent event) {
		executeProcedure(Collections.emptyMap());
	}
}