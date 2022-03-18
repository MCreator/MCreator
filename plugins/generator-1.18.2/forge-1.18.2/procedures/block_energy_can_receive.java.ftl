<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos) {
		AtomicBoolean _retval = new AtomicBoolean(false);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.canReceive()));
		return _retval.get();
	}
}.canReceiveEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->