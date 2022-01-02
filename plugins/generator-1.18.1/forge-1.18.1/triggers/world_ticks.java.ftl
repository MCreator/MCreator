<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase==TickEvent.Phase.END) {
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"world": "event.world",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}