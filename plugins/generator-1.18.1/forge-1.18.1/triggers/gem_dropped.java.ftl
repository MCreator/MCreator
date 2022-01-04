<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onGemDropped(ItemTossEvent event) {
		Player entity=event.getPlayer();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "entity.getX()",
			"y": "entity.getY()",
			"z": "entity.getZ()",
			"entity": "entity",
			"itemstack": "event.getEntityItem().getItem()",
			"world": "entity.level",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}