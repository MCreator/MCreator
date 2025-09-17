<@head>if (${input$entity} instanceof ServerPlayer _serverPlayer) {</@head>
	AdvancementHolder _adv${cbi} = _player.server.getAdvancements().get(ResourceLocation.parse("${generator.map(field$achievement, "achievements")}"));
	if (_adv${cbi} != null) {
		AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv${cbi});
		if (_ap.isDone()) {
			for (String criteria : _ap.getCompletedCriteria())
				_player.getAdvancements().revoke(_adv${cbi}, criteria);
		}
	}
<@tail>}</@tail>
