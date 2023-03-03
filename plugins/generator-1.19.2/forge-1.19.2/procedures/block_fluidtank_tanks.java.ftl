<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getBlockTanks(LevelAccessor _level, BlockPos _pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.getTanks()));
		return _retval.get();
	}
}.getBlockTanks(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->