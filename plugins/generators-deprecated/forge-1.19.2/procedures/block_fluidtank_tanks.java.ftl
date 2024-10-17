<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getBlockTanks(LevelAccessor level, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getTanks()));
		return _retval.get();
	}
}.getBlockTanks(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->