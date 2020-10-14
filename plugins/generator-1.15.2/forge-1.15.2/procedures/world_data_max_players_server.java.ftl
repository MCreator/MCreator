(new Object() {
	public int getMaxPlayers(World _world) {
		if (!(_world.isRemote()))
			return ServerLifecycleHooks.getCurrentServer().getMaxPlayers();
		else if (Minecraft.getInstance().getCurrentServerData() != null) {
			String[] popInfo = Minecraft.getInstance().getCurrentServerData().populationInfo.split("/§7");
			return Integer.parseInt(popInfo[1]);
		}
		return 8;
	}
}.getMaxPlayers((World) world))