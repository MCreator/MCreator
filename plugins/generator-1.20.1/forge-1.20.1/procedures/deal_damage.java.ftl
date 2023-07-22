{
	Entity _entToDamage = ${input$entity};
	_entToDamage.hurt(
		${input$damagesource},
		${opt.toFloat(input$amount)}
	);
}