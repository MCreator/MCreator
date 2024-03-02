<#include "mcelements.ftl">
<#-- @formatter:off -->
if (!world.isClientSide() && world.getServer() != null) {
	for (ItemStack itemstackiterator : world.getServer().getLootData().getLootTable(${toResourceLocation(input$location)})
			.getRandomItems(new LootParams.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY))) {
		${statement$foreach}
	}
}
<#-- @formatter:on -->