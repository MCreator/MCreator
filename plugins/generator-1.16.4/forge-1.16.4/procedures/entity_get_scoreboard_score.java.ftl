(new Object(){
	public int getScore(String score){
		if(${input$entity} instanceof PlayerEntity) {
			Scoreboard _sc = ((PlayerEntity)${input$entity}).getWorldScoreboard();
			ScoreObjective _so = _sc.getObjective(score);
			if (_so != null) {
				Score _scr = _sc.getOrCreateScore(((PlayerEntity)${input$entity}).getScoreboardName(), _so);
				return _scr.getScorePoints();
			}
		}
		return 0;
	}
}.getScore(${input$score}))