<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldTick(LevelTickEvent.Post event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"world": "event.getLevel()",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}