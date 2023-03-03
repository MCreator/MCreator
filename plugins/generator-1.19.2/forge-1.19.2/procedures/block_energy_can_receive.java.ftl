<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canReceiveEnergy(LevelAccessor _level, BlockPos _pos) {
		AtomicBoolean _retval = new AtomicBoolean(false);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ENERGY, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.canReceive()));
		return _retval.get();
	}
}.canReceiveEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->