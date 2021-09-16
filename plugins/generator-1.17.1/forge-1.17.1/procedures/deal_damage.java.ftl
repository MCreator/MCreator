<#if field$damagesource?has_content>
${input$entity}.hurt(${generator.map(field$damagesource, "damagesources")},(float)${input$amount});
<#else>
${input$entity}.hurt(DamageSource.GENERIC,(float)${input$amount});
</#if>