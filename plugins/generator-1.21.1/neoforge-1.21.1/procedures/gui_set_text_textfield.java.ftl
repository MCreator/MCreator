<#if w.hasElementsOfType("gui")>
if (${input$entity}.level().isClientSide && ${input$entity} instanceof Player _entity)
    ${JavaModName}Menus.sendGuistateUpdate(_entity, 0, "${field$textfield}", ${input$text});
</#if>