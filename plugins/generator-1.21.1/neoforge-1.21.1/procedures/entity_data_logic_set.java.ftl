<@head>if (${input$entity} instanceof ${generator.map(field$customEntity, "entities")} _datEntSetL) {</@head>
	_datEntSetL.getEntityData().set(${generator.map(field$customEntity, "entities")}.DATA_${field$accessor}, ${input$value});
<@tail>}</@tail>