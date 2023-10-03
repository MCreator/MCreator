(new Object() {
	public String getFriendlyFire(String _uuid) {
		if (world instanceof Level _level) {
			PlayerTeam _pt = _level.getScoreboard().getPlayersTeam(_uuid);
			if (_pt != null)
				return _pt.getName();
		}
		return "";
	}
}.getFriendlyFire(${input$entity}.getStringUUID()))