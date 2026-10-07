<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		<#-- event is only fired for hands holding an item, so the off hand is handled only if the main hand is empty -->
		if (event.getHand() != InteractionHand.MAIN_HAND && !event.getEntity().getMainHandItem().isEmpty())
			return;
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPos().getX()",
				"y": "event.getPos().getY()",
				"z": "event.getPos().getZ()",
				"world": "event.getLevel()",
				"entity": "event.getEntity()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		<#-- fix #6700, consume the interaction on the client if canceled so the off hand is not tried after the main hand -->
		if (event.isCanceled() && event.getLevel().isClientSide())
			event.setCancellationResult(InteractionResult.CONSUME);
	}