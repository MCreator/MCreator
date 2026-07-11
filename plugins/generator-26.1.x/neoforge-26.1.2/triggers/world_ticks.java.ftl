<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onWorldTick(LevelTickEvent.Post event) {
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"world": "event.getLevel()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}