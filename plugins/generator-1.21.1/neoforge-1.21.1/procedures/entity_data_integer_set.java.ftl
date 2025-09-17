<@head>if (${input$entity} instanceof ${generator.map(field$customEntity, "entities")} _datEntSetI) {</@head>
	_datEntSetI.getEntityData().set(${generator.map(field$customEntity, "entities")}.DATA_${field$accessor}, ${opt.toInt(input$value)});
<@tail>}</@tail>