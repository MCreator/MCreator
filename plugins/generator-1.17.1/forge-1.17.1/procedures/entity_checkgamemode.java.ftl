(new Object(){
	public boolean checkGamemode(Entity _ent){
		if(_ent instanceof ServerPlayer _serverPlayer) {
			return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.${generator.map(field$gamemode, "gamemodes")};
		} else if(_ent.level.isClientSide() && _ent instanceof AbstractClientPlayer _clientPlayer) {
			PlayerInfo _pi = Minecraft.getInstance().getConnection().getPlayerInfo(_clientPlayer.getGameProfile().getId());
			return _pi != null && _pi.getGameMode() == GameType.${generator.map(field$gamemode, "gamemodes")};
		}
		return false;
	}
}.checkGamemode(${input$entity}))