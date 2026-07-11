{
	Entity _ent = ${input$entity};
	if(_ent.level() instanceof ServerLevel _serverLevel) {
		_ent.hurtServer(_serverLevel, ${input$damagesource}, ${opt.toFloat(input$amount)});
	}
}