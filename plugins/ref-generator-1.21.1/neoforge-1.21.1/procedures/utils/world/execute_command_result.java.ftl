private static String executeCommandGetResult(LevelAccessor world, Vec3 pos, String command) {
	StringBuilder result = new StringBuilder();
	if (world instanceof ServerLevel level) {
		CommandSource dataConsumer = new CommandSource() {
			@Override public void sendSystemMessage(Component message) {
				result.append(message.getString());
			}

			@Override public boolean acceptsSuccess() {
				return true;
			}

			@Override public boolean acceptsFailure() {
				return true;
			}

			@Override public boolean shouldInformAdmins() {
				return false;
			}
		};
		level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(dataConsumer, pos, Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null), command);
	}
	return result.toString();
}