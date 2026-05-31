(${input$entity} instanceof ${generator.map(field$customEntity, "entities")} _datEntS ?
	_datEntS.getEntityData().get(${generator.map(field$customEntity, "entities")}.DATA_${field$accessor}) : "")