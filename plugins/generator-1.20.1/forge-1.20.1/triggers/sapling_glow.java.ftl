<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onSaplingGrow(SaplingGrowTreeEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"world": "event.getLevel()",
			"blockstate": "event.getLevel().getBlockState(event.getPos())",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}