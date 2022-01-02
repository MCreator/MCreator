<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityAttacked(LivingHurtEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
		    	"x": "entity.getX()",
		    	"y": "entity.getY()",
		    	"z": "entity.getZ()",
		    	"world": "entity.level",
				"amount": "event.getAmount()",
				"entity": "entity",
				"sourceentity": "event.getSource().getEntity()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}