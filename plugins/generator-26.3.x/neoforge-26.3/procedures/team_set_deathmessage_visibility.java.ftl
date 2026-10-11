<@head>if (world instanceof Level _level) {
	PlayerTeam _pt = _level.getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null) {
</@head>
		_pt.setDeathMessageVisibility(Team.Visibility.${field$visibility});
<@tail>
	}
}</@tail>