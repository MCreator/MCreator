<#-- @formatter:off -->
(new Object(){
	public String getResult(LevelAccessor world, Vec3 pos, String _command) {
		StringBuilder _result = new StringBuilder();
		if (world instanceof ServerLevel _level) {
			CommandSource _dataConsumer = new CommandSource() {
				@Override public void sendSystemMessage(Component message) {
					_result.append(message.getString());
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
			_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(_dataConsumer, pos, Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null), _command);
		}
		return _result.toString();
	}
}.getResult(world, new Vec3(${input$x}, ${input$y}, ${input$z}), ${input$command}))
<#-- @formatter:on -->