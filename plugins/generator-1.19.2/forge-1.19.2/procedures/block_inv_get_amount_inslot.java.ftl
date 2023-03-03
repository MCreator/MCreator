<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getAmount(LevelAccessor _world, BlockPos _pos, int _slotid) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _world.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
				.ifPresent(_capability -> _retval.set(_capability.getStackInSlot(_slotid).getCount()));
		return _retval.get();
	}
}.getAmount(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$slotid)}))
<#-- @formatter:on -->