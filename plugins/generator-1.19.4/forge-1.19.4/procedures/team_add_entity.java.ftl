{
	Level _level = entity.level();
	PlayerTeam _pt = _level.getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null)
		_level.getScoreboard().addPlayerToTeam(${input$entity}.getStringUUID(), _pt);
}