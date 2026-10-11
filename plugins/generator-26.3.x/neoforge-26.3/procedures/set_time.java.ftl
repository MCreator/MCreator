if (world instanceof ServerLevel _level) {
	ServerClockManager _clockManager = _level.getServer().clockManager();
	Optional<Holder<WorldClock>> _clock = _level.dimensionType().defaultClock();
	if (_clock.isPresent())
		_clockManager.setTotalTicks(_clock.get(), ${opt.toInt(input$time)});
}