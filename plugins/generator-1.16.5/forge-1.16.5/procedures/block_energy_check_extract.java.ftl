<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public int extractEnergySimulate(IWorld world, BlockPos pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.extractEnergy(_amount, true)));
		return _retval.get();
	}
}.extractEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)},(int)${input$amount}))
<#-- @formatter:on -->