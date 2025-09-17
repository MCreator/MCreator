<@head>if (${input$entity} instanceof ServerPlayer _player) {</@head>
	_player.setGameMode(GameType.${generator.map(field$gamemode, "gamemodes")});
<@tail>}</@tail>