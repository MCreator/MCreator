<#include "mcelements.ftl">
<#-- @formatter:off -->
if (!world.isClientSide() && world.getServer() != null) {
	BlockPos _bp = ${toBlockPos(input$x, input$y, input$z)};
	for (ItemStack itemstackiterator : world.getServer().getLootTables().get(${toResourceLocation(input$location)}).getRandomItems(new LootContext.Builder((ServerLevel) world)
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(_bp))
			.withParameter(LootContextParams.BLOCK_STATE, world.getBlockState(_bp))
			.withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(_bp))
			.create(LootContextParamSets.EMPTY))) {
		${statement$foreach}
	}
}
<#-- @formatter:on -->