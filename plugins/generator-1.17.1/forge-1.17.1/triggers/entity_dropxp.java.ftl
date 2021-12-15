<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onLivingDropXp(LivingExperienceDropEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
		    	"x": "entity.getX()",
		    	"y": "entity.getY()",
		    	"z": "entity.getZ()",
				"droppedexperience": "event.getDroppedExperience()",
				"originalexperience": "event.getOriginalExperience()",
				"sourceentity": "event.getAttackingPlayer()",
				"world": "entity.level",
				"entity": "entity",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}