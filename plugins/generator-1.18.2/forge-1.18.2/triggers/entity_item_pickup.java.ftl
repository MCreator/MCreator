<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPickup(EntityItemPickupEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPlayer().getX()",
			"y": "event.getPlayer().getY()",
			"z": "event.getPlayer().getZ()",
			"world": "event.getPlayer().level",
			"entity": "event.getPlayer()",
			"itemstack": "event.getItem().getItem()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}