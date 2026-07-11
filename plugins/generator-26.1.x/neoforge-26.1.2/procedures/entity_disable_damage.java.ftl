<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.getAbilities().invulnerable = ${input$condition};
<@tail>
	_player.onUpdateAbilities();
}</@tail>