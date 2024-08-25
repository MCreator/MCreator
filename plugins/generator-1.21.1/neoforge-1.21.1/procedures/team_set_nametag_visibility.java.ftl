if (world instanceof Level _level) {
	PlayerTeam _pt = _level.getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null)
		_pt.setNameTagVisibility(Team.Visibility.${field$visibility});
}