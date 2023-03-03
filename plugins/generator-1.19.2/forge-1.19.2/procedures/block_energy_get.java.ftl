<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getEnergyStored(LevelAccessor _level, BlockPos _pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ENERGY, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.getEnergyStored()));
		return _retval.get();
	}
}.getEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->