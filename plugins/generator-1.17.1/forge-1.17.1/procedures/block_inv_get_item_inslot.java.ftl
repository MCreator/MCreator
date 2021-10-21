<#-- @formatter:off -->
/*@ItemStack*/(new Object() {
	public ItemStack getItemStack(LevelAccessor world, BlockPos pos, int sltid) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		BlockEntity _ent = world.getBlockEntity(pos);
		if (_ent != null) {
			_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
				_retval.set(capability.getStackInSlot(sltid).copy());
			});
		}
		return _retval.get();
	}
}.getItemStack(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$slotid)}))
<#-- @formatter:on -->