<#if field$damagesource?has_content>
    ${input$entity}.attackEntityFrom(${generator.map(field$damagesource, "damagesources")},(float)${input$amount});
<#else>
    ${input$entity}.attackEntityFrom(DamageSource.GENERIC,(float)${input$amount});
</#if>