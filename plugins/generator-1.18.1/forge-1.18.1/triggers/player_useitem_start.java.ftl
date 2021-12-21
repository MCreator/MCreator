<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "entity.getX()",
            	"y": "entity.getY()",
            	"z": "entity.getZ()",
				"itemstack": "event.getItem()",
				"duration": "event.getDuration()",
				"world": "entity.level",
				"entity": "entity",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}