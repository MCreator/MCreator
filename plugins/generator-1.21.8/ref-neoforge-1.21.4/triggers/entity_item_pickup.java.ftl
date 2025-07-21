<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPickup(ItemEntityPickupEvent.Pre event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPlayer().getX()",
				"y": "event.getPlayer().getY()",
				"z": "event.getPlayer().getZ()",
				"world": "event.getPlayer().level()",
				"entity": "event.getPlayer()",
				"itemstack": "event.getItemEntity().getItem()",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}