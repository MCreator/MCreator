<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockBreak(BlockEvent.BreakEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"xpAmount": "event.getExpToDrop()",
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"px": "event.getPlayer().getX()",
			"py": "event.getPlayer().getY()",
			"pz": "event.getPlayer().getZ()",
			"world": "event.getWorld()",
			"entity": "event.getPlayer()",
			"blockstate": "event.getState()"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}