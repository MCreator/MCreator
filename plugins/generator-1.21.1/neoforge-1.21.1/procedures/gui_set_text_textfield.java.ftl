<#if w.hasElementsOfType("gui")>
if (${input$entity}.level().isClientSide && ${input$entity} instanceof Player _entity)
    ${JavaModName}Menus.sendMenuStateUpdate(_entity, "textfield", "${field$textfield}", ${input$text});
</#if>