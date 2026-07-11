<#if input$sourceentity == "null">
if (${input$entity} instanceof Mob _entity) _entity.setTarget(null);
<#else>
if (${input$entity} instanceof Mob _entity && ${input$sourceentity} instanceof LivingEntity _ent) _entity.setTarget(_ent);
</#if>