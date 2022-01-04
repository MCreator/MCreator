<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		Entity entity = event.getEntity();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"px": "entity.getX()",
			"py": "entity.getY()",
			"pz": "entity.getZ()",
			"world": "event.getWorld()",
			"entity": "entity",
			"blockstate": "event.getState()",
			"placedagainst": "event.getPlacedAgainst()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}