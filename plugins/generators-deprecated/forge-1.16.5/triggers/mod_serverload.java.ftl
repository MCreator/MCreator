@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.DEDICATED_SERVER}) private static class GlobalTrigger {
	@SubscribeEvent public static void init(FMLDedicatedServerSetupEvent event) {
		executeProcedure(Collections.emptyMap());
	}
}