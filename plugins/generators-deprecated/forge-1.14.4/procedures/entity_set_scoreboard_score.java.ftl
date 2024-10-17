{
	Entity _ent = ${input$entity};
	if(_ent instanceof PlayerEntity) {
		Scoreboard _sc = ((PlayerEntity)_ent).getWorldScoreboard();
		ScoreObjective _so = _sc.getObjective(${input$score});
		if (_so == null) {
			_so = _sc.addObjective(${input$score}, ScoreCriteria.DUMMY, new StringTextComponent(${input$score}), ScoreCriteria.RenderType.INTEGER);
		}
		Score _scr = _sc.getOrCreateScore(((PlayerEntity)_ent).getScoreboardName(), _so);
		_scr.setScorePoints((int)${input$value});
	}
}