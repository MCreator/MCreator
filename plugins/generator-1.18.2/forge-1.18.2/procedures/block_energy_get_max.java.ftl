<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getMaxEnergyStored()));
		return _retval.get();
	}
}.getMaxEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->