<#if input$damagesource?has_content>
${input$entity}.hurt(${input$damagesource}, ${opt.toFloat(input$amount)});
<#else>
${input$entity}.hurt(DamageSource.GENERIC, ${opt.toFloat(input$amount)});
</#if>