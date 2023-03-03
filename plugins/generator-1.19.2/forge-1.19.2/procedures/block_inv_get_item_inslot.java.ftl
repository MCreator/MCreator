<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@ItemStack*/(new Object() {
	public ItemStack getItemStack(LevelAccessor _world, BlockPos _pos, int _slotid) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		BlockEntity _ent = _world.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
				.ifPresent(_capability -> _retval.set(_capability.getStackInSlot(_slotid).copy()));
		return _retval.get();
	}
}.getItemStack(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$slotid)}))
<#-- @formatter:on -->