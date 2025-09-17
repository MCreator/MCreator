<#include "mcelements.ftl">
<#-- @formatter:off -->
<@head>if(${input$entity} instanceof ServerPlayer _serverPlayer) {</@head>
	{
		BlockPos _bpos = ${toBlockPos(input$x,input$y,input$z)};
		_serverPlayer.openMenu(new MenuProvider() {

			@Override public Component getDisplayName() {
				return Component.literal("${field$guiname}");
			}

			@Override public boolean shouldTriggerClientSideContainerClosingOnOpen() {
				return false;
			}

			@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				return new ${(field$guiname)}Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
			}

		}, _bpos);
	}
<@tail>}</@tail>
<#-- @formatter:on -->