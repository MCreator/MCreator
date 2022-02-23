<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPlayer().getX()",
			"y": "event.getPlayer().getY()",
			"z": "event.getPlayer().getZ()",
			"world": "event.getPlayer().level",
			"entity": "event.getPlayer()",
			"advancement": "event.getAdvancement()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}