<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.giveExperiencePoints(-(${opt.toInt(input$amount)}));
<@tail>}</@tail>