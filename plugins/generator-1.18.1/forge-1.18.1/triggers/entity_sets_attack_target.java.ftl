<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntitySetsAttackTarget(LivingSetAttackTargetEvent event) {
		LivingEntity sourceentity=event.getEntityLiving();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": " sourceentity.getX()",
			"y": " sourceentity.getY()",
			"z": " sourceentity.getZ()",
			"world": "sourceentity.level",
			"entity": "event.getTarget()",
			"sourceentity": " sourceentity",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}