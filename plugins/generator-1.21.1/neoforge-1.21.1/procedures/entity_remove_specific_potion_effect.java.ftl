<@head>if (${input$entity} instanceof LivingEntity _entity) {</@head>
	_entity.removeEffect(${generator.map(field$potion, "effects")});
<@tail>}</@tail>