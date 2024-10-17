if(entity instanceof EntityPlayer)
	((EntityPlayer)entity).setGameType(GameType.${generator.map(field$gamemode, "gamemodes")});