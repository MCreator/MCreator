<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		<#-- fix #5491, event is fired for both hands always, so we can filter by either -->
		if (event.getHand() != InteractionHand.MAIN_HAND) return;
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPos().getX()",
				"y": "event.getPos().getY()",
				"z": "event.getPos().getZ()",
				"world": "event.getLevel()",
				"entity": "event.getTarget()",
				"sourceentity": "event.getEntity()",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}