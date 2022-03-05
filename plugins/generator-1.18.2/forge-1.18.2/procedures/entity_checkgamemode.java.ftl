(new Object(){
	public boolean checkGamemode(Entity _ent){
		if(_ent instanceof ServerPlayer _serverPlayer) {
			return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.${generator.map(field$gamemode, "gamemodes")};
		} else if(_ent.level.isClientSide() && _ent instanceof Player _player) {
			return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
				&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.${generator.map(field$gamemode, "gamemodes")};
		}
		return false;
	}
}.checkGamemode(${input$entity}))