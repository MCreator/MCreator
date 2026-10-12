<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.getAbilities().mayfly = ${input$condition};
<@tail>
	_player.onUpdateAbilities();
}</@tail>