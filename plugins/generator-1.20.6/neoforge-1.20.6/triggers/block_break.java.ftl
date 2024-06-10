<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockBreak(BlockDropsEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"xpAmount": "event.getDroppedExperience()",
				"x": "event.getPos().getX()",
				"y": "event.getPos().getY()",
				"z": "event.getPos().getZ()",
				"px": "event.getPlayer().getX()",
				"py": "event.getPlayer().getY()",
				"pz": "event.getPlayer().getZ()",
				"world": "event.getLevel()",
				"entity": "event.getPlayer()",
				"blockstate": "event.getState()"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}