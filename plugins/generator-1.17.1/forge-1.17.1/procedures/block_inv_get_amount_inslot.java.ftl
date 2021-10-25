<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getAmount(LevelAccessor world, BlockPos pos, int sltid) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = world.getBlockEntity(pos);
		if (_ent != null) {
			_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
				_retval.set(capability.getStackInSlot(sltid).getCount());
			});
		}
		return _retval.get();
	}
}.getAmount(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$slotid)}))
<#-- @formatter:on -->