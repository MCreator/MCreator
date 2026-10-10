private static boolean hasEntityPermissionLevel(Entity entity, int permissionLevel) {
	if (entity instanceof Player _player) {
		return switch (permissionLevel) {
			case 0 -> true;
			case 1 -> _player.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
			case 2 -> _player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
			case 3 -> _player.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
			default -> _player.permissions().hasPermission(Permissions.COMMANDS_OWNER);
		};
	}
	return false;
}