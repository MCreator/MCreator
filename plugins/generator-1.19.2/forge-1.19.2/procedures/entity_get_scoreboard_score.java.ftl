/*@int*/(new Object(){
	public int getScore(String _score, Entity _ent){
		Scoreboard _sc = _ent.getLevel().getScoreboard();
		Objective _so = _sc.getObjective(_score);
		if (_so != null)
			return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
		return 0;
	}
}.getScore(${input$score}, ${input$entity}))