<#-- @formatter:off -->
(new Object(){
	public String getResult(Entity _ent, String _command) {
		StringBuilder _result = new StringBuilder();
		if(!_ent.level().isClientSide() && _ent.getServer() != null) {
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
			_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(
					_dataConsumer, _ent.position(), _ent.getRotationVector(),
					_ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
					_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent
			), _command);
		}
		return _result.toString();
	}
}.getResult(${input$entity}, ${input$command}))
<#-- @formatter:on -->