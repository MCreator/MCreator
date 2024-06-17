<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBlockBreak(BlockDropsEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"xpAmount": "event.getDroppedExperience()",
				"x": "event.getPos().getX()",
				"y": "event.getPos().getY()",
				"z": "event.getPos().getZ()",
				"px": "event.getBreaker() != null ? event.getBreaker().getX() : event.getPos().getX()",
				"py": "event.getBreaker() != null ? event.getBreaker().getY() : event.getPos().getY()",
				"pz": "event.getBreaker() != null ? event.getBreaker().getZ() : event.getPos().getZ()",
				"world": "event.getLevel()",
				"entity": "event.getBreaker()",
				"blockstate": "event.getState()"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}