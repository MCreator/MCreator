<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canExtractEnergy(IWorld world, BlockPos pos) {
		AtomicBoolean _retval = new AtomicBoolean(false);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.canExtract()));
		return _retval.get();
	}
}.canExtractEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->