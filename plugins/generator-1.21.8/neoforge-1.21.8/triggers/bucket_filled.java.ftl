<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onBucketFill(PlayerInteractEvent.RightClickItem event) {
		if (event.getItemStack().getItem() == Items.BUCKET) {
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
					"x": "event.getEntity().getX()",
					"y": "event.getEntity().getY()",
					"z": "event.getEntity().getZ()",
					"world": "event.getLevel()",
					"itemstack": "event.getItemStack()",
					"entity": "event.getEntity()",
					"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}