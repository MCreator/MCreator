<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPlayerXPLevelChange(PlayerXpEvent.LevelChange event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "entity.getX()",
            	"y": "entity.getY()",
            	"z": "entity.getZ()",
				"world": "entity.level",
				"entity": "entity",
				"amount": "event.getLevels()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}