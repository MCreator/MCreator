{
    Entity _ent = ${input$entity};
	Scoreboard _sc = _ent.getLevel().getScoreboard();
	Objective _so = _sc.getObjective(${input$score});
	if (_so == null)
		_so = _sc.addObjective(${input$score}, ObjectiveCriteria.DUMMY, new TextComponent(${input$score}), ObjectiveCriteria.RenderType.INTEGER);
	_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(${opt.toInt(input$value)});
}