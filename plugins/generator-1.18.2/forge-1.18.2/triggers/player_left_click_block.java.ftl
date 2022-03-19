<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getHand() != event.getPlayer().getUsedItemHand())
			return;
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"world": "event.getWorld()",
			"entity": "event.getPlayer()",
			"direction": "event.getFace()",
			"blockstate": "event.getWorld().getBlockState(event.getPos())",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}