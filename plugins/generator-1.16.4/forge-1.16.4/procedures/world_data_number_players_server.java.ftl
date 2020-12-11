(world instanceof World && ((World) world).isRemote?Minecraft.getInstance().getConnection().getPlayerInfoMap().size():
	ServerLifecycleHooks.getCurrentServer().getCurrentPlayerCount())