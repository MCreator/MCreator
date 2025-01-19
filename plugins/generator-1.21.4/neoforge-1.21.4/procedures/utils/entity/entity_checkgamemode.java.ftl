private static GameType getEntityGameType(Entity entity){
	if(entity instanceof ServerPlayer serverPlayer) {
		return serverPlayer.gameMode.getGameModeForPlayer();
	} else if(entity instanceof Player player && player.level().isClientSide()) {
		PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId());
		if (playerInfo != null)
			return playerInfo.getGameMode();
	}
	return null;
}