<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onGemDropped(ItemTossEvent event) {
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPlayer().getX()",
				"y": "event.getPlayer().getY()",
				"z": "event.getPlayer().getZ()",
				"world": "event.getPlayer().level()",
				"entity": "event.getPlayer()",
				"itemstack": "event.getEntity().getItem()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}