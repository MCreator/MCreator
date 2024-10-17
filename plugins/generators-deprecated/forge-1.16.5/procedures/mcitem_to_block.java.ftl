<#include "mcitems.ftl">
<#-- @formatter:off -->
/*@BlockState*/(new Object() {
	public BlockState toBlock(ItemStack _stk) {
		if(_stk.getItem() instanceof BlockItem) {
		    return ((BlockItem) _stk.getItem()).getBlock().getDefaultState();
		}
		return Blocks.AIR.getDefaultState();
	}
}.toBlock(${mappedMCItemToItemStackCode(input$source, 1)}))
<#-- @formatter:on -->