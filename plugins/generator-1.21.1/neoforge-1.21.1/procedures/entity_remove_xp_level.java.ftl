<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.giveExperienceLevels(-(${opt.toInt(input$xpamount)}));
<@tail>}</@tail>