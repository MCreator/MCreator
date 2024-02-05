<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getAmount(LevelAccessor world, BlockPos pos, int slotid) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = world.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null)
				.ifPresent(capability -> _retval.set(capability.getStackInSlot(slotid).getCount()));
		return _retval.get();
	}
}.getAmount(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$slotid)}))
<#-- @formatter:on -->