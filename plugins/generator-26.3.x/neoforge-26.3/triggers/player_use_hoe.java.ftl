<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseHoe(UseItemOnBlockEvent event) {
		if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && event.getPlayer() != null) {
			Holder<BlockTransformer> transformer = event.getItemStack().get(DataComponents.BLOCK_TRANSFORMER);
			if (transformer != null && transformer.is(BlockTransformers.HOE)) {
				<#assign dependenciesCode>
					<@procedureDependenciesCode dependencies, {
						"x": "event.getPos().getX()",
						"y": "event.getPos().getY()",
						"z": "event.getPos().getZ()",
						"world": "event.getLevel()",
						"entity": "event.getPlayer()",
						"blockstate": "event.getLevel().getBlockState(event.getPos())",
						"event": "event"
					}/>
				</#assign>
				execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
			}
		}
	}