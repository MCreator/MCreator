{
	Entity _entityTeam = ${input$entity};
	PlayerTeam _pt = _entityTeam.level().getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null) {
		if (_entityTeam instanceof Player _player)
			_entityTeam.level().getScoreboard().addPlayerToTeam(_player.getGameProfile().getName(), _pt);
		else
			_entityTeam.level().getScoreboard().addPlayerToTeam(_entityTeam.getStringUUID(), _pt);
	}
}