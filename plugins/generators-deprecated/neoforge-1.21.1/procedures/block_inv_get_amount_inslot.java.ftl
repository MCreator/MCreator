<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getAmount(LevelAccessor world, BlockPos pos, int slotid) {
		if (world instanceof ILevelExtension _ext) {
			IItemHandler _itemHandler = _ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if (_itemHandler != null)
				return _itemHandler.getStackInSlot(slotid).getCount();
		}
		return 0;
	}
}.getAmount(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$slotid)}))
<#-- @formatter:on -->