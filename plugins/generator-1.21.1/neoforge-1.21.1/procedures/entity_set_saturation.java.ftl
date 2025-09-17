<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.getFoodData().setSaturation(${opt.toFloat(input$amount)});
<@tail>}</@tail>