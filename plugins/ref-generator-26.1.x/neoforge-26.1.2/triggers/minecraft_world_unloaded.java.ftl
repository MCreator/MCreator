<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldUnload(net.neoforged.neoforge.event.level.LevelEvent.Unload event) {
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"world": "event.getLevel()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}