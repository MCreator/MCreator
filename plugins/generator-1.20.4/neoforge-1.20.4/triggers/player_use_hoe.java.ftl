<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseHoe(BlockEvent.BlockToolModificationEvent event) {
		if (!event.isSimulated() && event.getToolAction() == ToolActions.HOE_TILL) {
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
				"x": "event.getContext().getClickedPos().getX()",
				"y": "event.getContext().getClickedPos().getY()",
				"z": "event.getContext().getClickedPos().getZ()",
				"world": "event.getPlayer().level()",
				"entity": "event.getPlayer()",
				"blockstate": "event.getPlayer().level().getBlockState(event.getContext().getClickedPos())",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}