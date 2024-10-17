{
	Entity _entityTeam = ${input$entity};
	PlayerTeam _pt = _entityTeam.level().getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null)
		_entityTeam.level().getScoreboard().removePlayerFromTeam(_entityTeam.getStringUUID(), _pt);
}