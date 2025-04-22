private static String executeCommandGetResult(Entity entity, String command) {
	StringBuilder result = new StringBuilder();
	if(!entity.level().isClientSide() && entity.getServer() != null) {
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
		entity.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(
				dataConsumer, entity.position(), entity.getRotationVector(),
				entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null, 4,
				entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity
		), command);
	}
	return result.toString();
}