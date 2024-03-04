<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getEntity().getX()",
			"y": "event.getEntity().getY()",
			"z": "event.getEntity().getZ()",
			"world": "event.getEntity().level()",
			"dimension": "event.getDimension()",
			"entity": "event.getEntity()",
			"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}