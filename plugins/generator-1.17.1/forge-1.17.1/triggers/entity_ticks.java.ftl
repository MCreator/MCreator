<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityTick(LivingEvent.LivingUpdateEvent event){
		Entity entity = event.getEntityLiving();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"world": "entity.level",
			"entity": "entity",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}