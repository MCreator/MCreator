<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	if(${input$entity} instanceof ServerPlayer _ent) {
		BlockPos _bpos = ${toBlockPos(input$x,input$y,input$z)};
		NetworkHooks.openGui((ServerPlayer) _ent, new MenuProvider() {
			@Override public Component getDisplayName() {
				return new TextComponent("${field$guiname}");
			}
			@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				return new ${(field$guiname)}Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
			}
		}, _bpos);
	}
}
<#-- @formatter:on -->