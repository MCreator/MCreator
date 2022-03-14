<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object() {
	public int getBlockTanks(BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getTanks()));
		return _retval.get();
	}
}.getBlockTanks(${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->