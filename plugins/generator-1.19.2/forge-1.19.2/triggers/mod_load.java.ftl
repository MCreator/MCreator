<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Procedure {
	@SubscribeEvent public static void init(FMLCommonSetupEvent event) {
		execute();
	}