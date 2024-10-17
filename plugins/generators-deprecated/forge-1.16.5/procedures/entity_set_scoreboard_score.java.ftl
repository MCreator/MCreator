{
	Entity _ent = ${input$entity};
	Scoreboard _sc = _ent.world.getScoreboard();
	ScoreObjective _so = _sc.getObjective(${input$score});
	if (_so == null)
		_so = _sc.addObjective(${input$score}, ScoreCriteria.DUMMY, new StringTextComponent(${input$score}), ScoreCriteria.RenderType.INTEGER);
	_sc.getOrCreateScore(_ent.getScoreboardName(), _so).setScorePoints((int)${input$value});
}