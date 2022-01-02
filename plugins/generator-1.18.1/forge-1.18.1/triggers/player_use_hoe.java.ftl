<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseHoe(UseHoeEvent event) {
		Player entity=event.getPlayer();
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getContext().getClickedPos().getX()",
			"y": "event.getContext().getClickedPos().getY()",
			"z": "event.getContext().getClickedPos().getZ()",
			"world": "entity.level",
			"entity": "entity",
			"blockstate": "entity.level.getBlockState(event.getContext().getClickedPos())",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}