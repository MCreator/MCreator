<#if w.hasElementsOfType("gui")>
if (${input$entity}.level().isClientSide && ${input$entity} instanceof Player _entity) <#-- For now, you can only set the value on the client, as before;
                                                                                            during synchronization, you can only set it from the server. -->
    ${JavaModName}Menus.sendMenuStateUpdate(_entity, "textfield", "${field$textfield}", ${input$text});
</#if>