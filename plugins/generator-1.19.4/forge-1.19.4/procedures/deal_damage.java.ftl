{
	Entity _entToDamage = ${input$entity};
	_entToDamage.hurt(
		<#if input$damagesource?has_content>
			${input$damagesource}
		<#else>
			DamageTypes.GENERIC
		</#if>,
		${opt.toFloat(input$amount)}
	);
}