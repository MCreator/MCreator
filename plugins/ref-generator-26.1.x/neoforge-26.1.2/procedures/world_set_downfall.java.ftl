if (world instanceof ServerLevel _level && _level.getServer() != null) {
	_level.getServer().setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(_level.getRandom()), true, false);
}