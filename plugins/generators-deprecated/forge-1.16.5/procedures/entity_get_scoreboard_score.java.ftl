(new Object(){
	public int getScore(String score, Entity _ent){
		Scoreboard _sc = _ent.world.getScoreboard();
		ScoreObjective _so = _sc.getObjective(score);
		if (_so != null)
			return _sc.getOrCreateScore(_ent.getScoreboardName(), _so).getScorePoints();
		return 0;
	}
}.getScore(${input$score}, ${input$entity}))