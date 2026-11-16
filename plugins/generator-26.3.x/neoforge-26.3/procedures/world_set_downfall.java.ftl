if (world instanceof ServerLevel _level && _level.getServer() != null) {
	if (${input$condition})
		_level.getServer().setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(_level.getRandom()), true, false);
	else
		_level.getServer().setWeatherParameters(ServerLevel.RAIN_DELAY.sample(_level.getRandom()), 0, false, false);
}