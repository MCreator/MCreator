<#if w.hasElementsOfType("gui")>
if (${input$entity} instanceof ServerPlayer _entity && _entity.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu)
	_menu.sendMenuStateUpdate(0, "${field$textfield}", ${input$text}, true);
</#if>