<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END){
			Entity entity = event.player;
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
	}