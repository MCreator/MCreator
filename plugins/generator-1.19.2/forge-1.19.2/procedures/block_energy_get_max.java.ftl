<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getMaxEnergyStored(LevelAccessor _level, BlockPos _pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ENERGY, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.getMaxEnergyStored()));
		return _retval.get();
	}
}.getMaxEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->