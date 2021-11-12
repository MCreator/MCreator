<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"world": "event.getWorld()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}