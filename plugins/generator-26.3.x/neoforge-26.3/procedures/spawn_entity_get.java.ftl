<#include "mcelements.ftl">
<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
(world instanceof ServerLevel _level${cbi} ? ${entity}.spawn(_level${cbi}, ${toBlockPos(input$x,input$y,input$z)}, EntitySpawnReason.MOB_SUMMONED) : null)
<#else>
null
</#if>