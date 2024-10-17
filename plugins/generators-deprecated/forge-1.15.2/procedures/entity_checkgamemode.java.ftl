(new Object(){
	public boolean checkGamemode(Entity _ent){
		if(_ent instanceof ServerPlayerEntity) {
			return ((ServerPlayerEntity) _ent).interactionManager.getGameType() == GameType.${generator.map(field$gamemode, "gamemodes")};
		} else if(_ent instanceof PlayerEntity && _ent.world.isRemote) {
			NetworkPlayerInfo _npi = Minecraft.getInstance().getConnection().getPlayerInfo(((AbstractClientPlayerEntity) _ent).getGameProfile().getId());
			return _npi != null && _npi.getGameType() == GameType.${generator.map(field$gamemode, "gamemodes")};
		}
		return false;
	}
}.checkGamemode(${input$entity}))