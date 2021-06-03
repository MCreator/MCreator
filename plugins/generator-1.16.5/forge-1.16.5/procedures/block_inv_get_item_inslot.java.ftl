<#-- @formatter:off -->
/*@ItemStack*/(new Object() {
	public ItemStack getItemStack(BlockPos pos, int sltid) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null) {
			_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
				_retval.set(capability.getStackInSlot(sltid).copy());
			});
		}
		return _retval.get();
	}
}.getItemStack(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)(${input$slotid})))
<#-- @formatter:on -->