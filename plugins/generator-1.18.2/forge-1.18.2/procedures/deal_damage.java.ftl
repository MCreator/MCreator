<#if input$damagesource?has_content>
${input$entity}.hurt(${generator.map(input$damagesource, "damagesources")}, ${opt.toFloat(input$amount)});
<#else>
${input$entity}.hurt(DamageSource.GENERIC, ${opt.toFloat(input$amount)});
</#if>