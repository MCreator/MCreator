<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@ItemStack*/(new Object() {
	public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int slotid) {
		if (world instanceof ILevelExtension _ext) {
			IItemHandler _itemHandler = _ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if (_itemHandler != null)
				return _itemHandler.getStackInSlot(slotid).copy();
		}
		return ItemStack.EMPTY;
	}
}.getItemStack(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$slotid)}))
<#-- @formatter:on -->