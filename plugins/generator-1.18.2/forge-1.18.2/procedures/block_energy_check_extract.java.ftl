<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int extractEnergySimulate(LevelAccessor level, BlockPos pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.extractEnergy(_amount, true)));
		return _retval.get();
	}
}.extractEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->