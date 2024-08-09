<#if field$type == "UNDEAD">
(${input$entity}.getType().is(EntityTypeTags.UNDEAD))
<#elseif field$type == "ARTHROPOD">
(${input$entity}.getType().is(EntityTypeTags.ARTHROPOD))
<#elseif field$type == "ILLAGER">
(${input$entity}.getType().is(EntityTypeTags.ILLAGER))
<#elseif field$type == "WATER">
(${input$entity}.getType().is(EntityTypeTags.AQUATIC))
<#else>
false<#-- fallback for workspaces using UNDEFINED type, as this one no longer exists in 1.20.6+ -->
</#if>