<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onExplode(ExplosionEvent.Detonate event) {
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getExplosion().center().x()",
				"y": "event.getExplosion().center().y()",
				"z": "event.getExplosion().center().z()",
				"world": "event.getLevel()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}