(new Object() {
	public boolean getFriendlyFire(String teamName) {
		if (world instanceof Level _level) {
			PlayerTeam _pt = _level.getScoreboard().getPlayerTeam(teamName);
			return _pt != null && _pt.isAllowFriendlyFire();
		}
		return false;
	}
}.getFriendlyFire(${input$name}))