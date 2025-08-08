if(${input$entity} instanceof ServerPlayer _player && _player.level() instanceof ServerLevel _level) {
	AdvancementHolder _adv = _level.getServer().getAdvancements().get(ResourceLocation.parse("${generator.map(field$achievement, "achievements")}"));
	if (_adv != null) {
		AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
		if (!_ap.isDone()) {
			for (String criteria : _ap.getRemainingCriteria())
				_player.getAdvancements().award(_adv, criteria);
		}
	}
}