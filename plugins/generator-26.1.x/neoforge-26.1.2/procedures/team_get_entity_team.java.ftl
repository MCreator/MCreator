(${input$entity} instanceof LivingEntity _teamEnt && _teamEnt.level().getScoreboard()
	.getPlayersTeam(_teamEnt instanceof Player _pl ? _pl.getGameProfile().name() : _teamEnt.getStringUUID()) != null ?
		_teamEnt.level().getScoreboard().getPlayersTeam(_teamEnt instanceof Player _pl ? _pl.getGameProfile().name() : _teamEnt.getStringUUID()).getName() : "")