/*@int*/(new Object(){
	public int getScore(String score, Entity _ent){
		Scoreboard _sc = _ent.level().getScoreboard();
		Objective _so = _sc.getObjective(score);
		if (_so != null)
			return _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(_ent.getScoreboardName()), _so).get();
		return 0;
	}
}.getScore(${input$score}, ${input$entity}))