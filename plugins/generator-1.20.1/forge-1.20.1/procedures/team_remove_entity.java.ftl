{
	Entity _entity${cbi} = ${input$entity};
	PlayerTeam _pt = _entity${cbi}.level().getScoreboard().getPlayerTeam(${input$name});
	if (_pt != null)
		_entity${cbi}.level().getScoreboard().removePlayerFromTeam(_entity${cbi}.getStringUUID(), _pt);
}