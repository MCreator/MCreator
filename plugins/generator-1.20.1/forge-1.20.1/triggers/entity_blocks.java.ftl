<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void whenEntityBlocksWithShield(ShieldBlockEvent event) {
		if (event!=null && event.getEntity()!=null) {
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getEntity().getX()",
				"y": "event.getEntity().getY()",
				"z": "event.getEntity().getZ()",
				"world": "event.getEntity().level()",
				"entity": "event.getEntity()",
				"damagesource": "event.getDamageSource()",
				"sourceentity": "event.getDamageSource().getEntity()",
				"immediatesourceentity": "event.getDamageSource().getDirectEntity()",
				"originalblockedamount": "event.getOriginalBlockedDamage()",
				"blockedamount": "event.getBlockedDamage()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}