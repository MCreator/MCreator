(new Object(){
	public int getScore(String score){
		if(entity instanceof EntityPlayer) {
			Scoreboard _sc = ((EntityPlayer)entity).getWorldScoreboard();
			ScoreObjective _so = _sc.getObjective(score);
			if (_so != null) {
				Score _scr = _sc.getOrCreateScore(((EntityPlayer)entity).getGameProfile().getName(), _so);
				return _scr.getScorePoints();
			}
		}
		return 0;
	}
}.getScore(${input$score}))