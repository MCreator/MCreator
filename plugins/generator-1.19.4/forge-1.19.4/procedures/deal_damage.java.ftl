{
	Entity _entToDamage = ${input$entity};
    _entToDamage.hurt(new DamageSource(
        _entToDamage.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(
            <#if input$damagesource?has_content>
                ${generator.map(input$damagesource, "damagesources")}
            <#else>
                DamageTypes.GENERIC
            </#if>
        )),
        ${opt.toFloat(input$amount)}
    );
}