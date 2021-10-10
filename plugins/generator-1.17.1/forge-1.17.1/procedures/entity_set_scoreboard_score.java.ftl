if(${input$entity} instanceof Player _player) {
	Scoreboard _sc = _player.getScoreboard();
	Objective _so = _sc.getObjective(${input$score});
	if (_so == null)
		_so = _sc.addObjective(${input$score}, ObjectiveCriteria.DUMMY, new TextComponent(${input$score}), ObjectiveCriteria.RenderType.INTEGER);
	Score _scr = _sc.getOrCreatePlayerScore(_player.getScoreboardName(), _so);
	_scr.setScore(${opt.toInt(input$value)});
}