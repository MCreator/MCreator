/*@int*/(new Object(){
	public int getScore(String score, Entity _ent){
		if(_ent instanceof Player _player) {
			Scoreboard _sc = _player.getScoreboard();
			Objective _so = _sc.getObjective(score);
			if (_so != null) {
				Score _scr = _sc.getOrCreatePlayerScore(_player.getScoreboardName(), _so);
				return _scr.getScore();
			}
		}
		return 0;
	}
}.getScore(${input$score}, ${input$entity}))