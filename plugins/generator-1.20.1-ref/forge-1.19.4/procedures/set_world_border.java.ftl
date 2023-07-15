public static void setWorldBorder(int x, int y, int z, int size) {
    WorldBorder worldBorder = event.player.getServer().getLevel(event.player.getCommandSenderWorld().dimension()).getWorldBorder();
    worldBorder.setCenter(x, y, z);
    worldBorder.setSize(size);
}
