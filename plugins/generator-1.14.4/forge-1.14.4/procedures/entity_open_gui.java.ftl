<#-- @formatter:off -->
{
	Entity _ent = ${input$entity};
	if(_ent instanceof ServerPlayerEntity) {
		BlockPos _bpos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
		NetworkHooks.openGui((ServerPlayerEntity) _ent, new INamedContainerProvider() {
			@Override public ITextComponent getDisplayName() {
				return new StringTextComponent("${field$guiname}");
			}
			@Override public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
				return new ${(field$guiname)}Gui.GuiContainerMod(id, inventory, new PacketBuffer(Unpooled.buffer()).writeBlockPos(_bpos));
			}
		}, _bpos);
	}
}
<#-- @formatter:on -->