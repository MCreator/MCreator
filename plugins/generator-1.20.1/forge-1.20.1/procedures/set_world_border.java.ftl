 public static void setWorldBorder(Player player, int x, int z, int size) {
        MinecraftServer server = player.getServer();
        WorldBorder worldBorder;

        if (server == null || !(server.getLevel(player.level.dimension()) instanceof ServerLevel)) {
            return;
        }

        ServerLevel world = (ServerLevel) server.getLevel(player.level.dimension());
        worldBorder = world.getWorldBorder();

        worldBorder.setCenter(x, z);
        worldBorder.setSize(size);
    }
}
