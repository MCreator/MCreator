<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState toBlock(ItemStack _bckt) {
		if(_bckt.getItem() instanceof BucketItem) {
		    return ((BucketItem) _bckt.getItem()).getFluid().getDefaultState().getBlockState();
		}
		return Blocks.AIR.getDefaultState();
	}
}.toBlock(${mappedMCItemToItemStackCode(input$source, 1)}))