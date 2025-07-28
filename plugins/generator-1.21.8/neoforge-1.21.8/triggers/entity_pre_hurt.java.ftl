<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityAttacked(LivingDamageEvent.Post event) {
		if (event.getEntity() != null) {
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
					"x": "event.getEntity().getX()",
					"y": "event.getEntity().getY()",
					"z": "event.getEntity().getZ()",
					"world": "event.getEntity().level()",
					"amount": "event.getOriginalDamage()",
					"entity": "event.getEntity()",
					"damagesource": "event.getSource()",
					"sourceentity": "event.getSource().getEntity()",
					"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}