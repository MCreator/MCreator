<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onUseHoe(UseItemOnBlockEvent event) {
		if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && event.getPlayer() != null) {
			Holder<BlockTransformer> transformer = event.getItemStack().get(DataComponents.BLOCK_TRANSFORMER);
			if (transformer != null && transformer.is(BlockTransformers.HOE) && canTill(event, transformer)) {
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

	<#-- mirrors BlockTransformer#transformBlock checks, so the trigger only fires when the block will actually be tilled -->
	private static boolean canTill(UseItemOnBlockEvent event, Holder<BlockTransformer> transformer) {
		if (event.getHand() == InteractionHand.MAIN_HAND && event.getPlayer().getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !event.getPlayer().isSecondaryUseActive())
			return false;
		for (BlockTransformer.BlockTransformData transform : DataMapHooks.getAllTransformers(transformer)) {
			if (!transform.disallowedFaces().contains(event.getFace()) && transform.blockStateProvider().value()
					.getOptionalState(event.getLevel(), event.getLevel().getRandom(), event.getPos()) != null)
				return true;
		}
		return false;
	}