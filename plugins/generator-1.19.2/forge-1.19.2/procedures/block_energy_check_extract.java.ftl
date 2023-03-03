<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int extractEnergySimulate(LevelAccessor _level, BlockPos _pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ENERGY, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.extractEnergy(_amount, true)));
		return _retval.get();
	}
}.extractEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->