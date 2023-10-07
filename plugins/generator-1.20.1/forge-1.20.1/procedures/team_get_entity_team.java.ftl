(new Object() {
	public String getFriendlyFire(Level _level, String _uuid) {
		PlayerTeam _pt = _level.getScoreboard().getPlayersTeam(_uuid);
		if (_pt != null)
			return _pt.getName();
		return "";
	}
}.getFriendlyFire(${input$entity}.level(), ${input$entity}.getStringUUID()))