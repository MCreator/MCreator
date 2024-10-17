if(entity instanceof EntityPlayer) {
	Scoreboard _sc = ((EntityPlayer)entity).getWorldScoreboard();
	ScoreObjective _so = _sc.getObjective(${input$score});
	if (_so == null) {
		_so = _sc.addScoreObjective(${input$score}, ScoreCriteria.DUMMY);
	}
	Score _scr = _sc.getOrCreateScore(((EntityPlayer)entity).getGameProfile().getName(), _so);
	_scr.setScorePoints((int)${input$value});
}