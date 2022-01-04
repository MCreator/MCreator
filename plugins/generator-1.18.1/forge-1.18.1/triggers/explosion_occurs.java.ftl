<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onExplode(ExplosionEvent.Detonate event) {
		Explosion explosion = event.getExplosion();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "explosion.getPosition().x",
			"y": "explosion.getPosition().y",
			"z": "explosion.getPosition().z",
			"world": "event.getWorld()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}