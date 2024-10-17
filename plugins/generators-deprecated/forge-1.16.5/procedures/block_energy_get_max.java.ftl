<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public int getMaxEnergyStored(IWorld world, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getMaxEnergyStored()));
		return _retval.get();
	}
}.getMaxEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->