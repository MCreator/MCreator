<#if w.hasElementsOfType("gui")>
if (${input$entity} instanceof Player _entity && _entity.containerMenu instanceof ${JavaModName}Menus.MenuAccessor menu)
	menu.sendMenuStateUpdate(_entity.level(), 0, "${field$textfield}", ${input$text});
</#if>