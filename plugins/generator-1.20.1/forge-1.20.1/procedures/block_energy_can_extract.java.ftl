<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos) {
		AtomicBoolean _retval = new AtomicBoolean(false);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.canExtract()));
		return _retval.get();
	}
}.canExtractEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->