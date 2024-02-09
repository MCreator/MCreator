<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onExplode(ExplosionEvent.Detonate event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getExplosion().center().x()",
			"y": "event.getExplosion().center().y()",
			"z": "event.getExplosion().center().z()",
			"world": "event.getLevel()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}