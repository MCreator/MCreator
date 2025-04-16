<#if w.hasElementsOfType("gui")>
if (${input$entity} instanceof Player _entity)
    ${JavaModName}Menus.sendMenuStateUpdate(_entity, 0, "${field$textfield}", ${input$text});
</#if>