<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPos().getX()",
				"y": "event.getPos().getY()",
				"z": "event.getPos().getZ()",
				"px": "event.getPlayer() != null ? event.getPlayer().getX() : event.getPos().getX()",
				"py": "event.getPlayer() != null ? event.getPlayer().getY() : event.getPos().getY()",
				"pz": "event.getPlayer() != null ? event.getPlayer().getZ() : event.getPos().getZ()",
				"world": "event.getLevel()",
				"entity": "event.getPlayer()",
				"blockstate": "event.getState()"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}