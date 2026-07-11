(${input$entity} instanceof ${generator.map(field$customEntity, "entities")} _datEntI ?
	_datEntI.getEntityData().get(${generator.map(field$customEntity, "entities")}.DATA_${field$accessor}) : 0)