(world instanceof World && world.isRemote()?Minecraft.getInstance().getConnection().getPlayerInfoMap().size():
	ServerLifecycleHooks.getCurrentServer().getCurrentPlayerCount())