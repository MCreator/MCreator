{
	if (${input$player} instanceof Player && world instanceof Level _level) {
		PlayerTeam _pt = _level.getScoreboard().getPlayerTeam(${input$name});
		if (_pt != null)
	    	_level.getScoreboard().addPlayerToTeam(${input$player}.getStringUUID(), _pt);
	}
}