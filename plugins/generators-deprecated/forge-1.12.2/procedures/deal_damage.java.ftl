<#if field$damagesource?has_content>
    entity.attackEntityFrom(DamageSource.${generator.map(field$damagesource, "damagesources")},(float)${input$amount});
<#else>
    entity.attackEntityFrom(DamageSource.GENERIC,(float)${input$amount});
</#if>