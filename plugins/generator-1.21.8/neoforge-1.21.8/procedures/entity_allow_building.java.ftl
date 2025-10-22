<@head>if (${input$entity} instanceof Player _player) {</@head>
	_player.getAbilities().mayBuild = ${input$condition};
<@tail>
	_player.onUpdateAbilities();
}</@tail>