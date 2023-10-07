(new Object() {
	public String getFriendlyFire(Entity _entity) {
		PlayerTeam _pt = _entity.level.getScoreboard().getPlayersTeam(_entity.getStringUUID());
		if (_pt != null)
			return _pt.getName();
		return "";
	}
}.getFriendlyFire(${input$entity}))