<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	int _amount = (int)${input$amount};
	if (_ent != null)
		_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
			capability.receiveEnergy(_amount, false));
}
<#-- @formatter:on -->