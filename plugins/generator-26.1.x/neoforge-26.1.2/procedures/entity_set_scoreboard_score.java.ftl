{
	Entity _ent = ${input$entity};
	Scoreboard _sc = _ent.level().getScoreboard();
	Objective _so = _sc.getObjective(${input$score});
	if (_so == null)
		_so = _sc.addObjective(${input$score}, ObjectiveCriteria.DUMMY, Component.literal(${input$score}), ObjectiveCriteria.RenderType.INTEGER, true, null);
	_sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _so).set(${opt.toInt(input$value)});
}