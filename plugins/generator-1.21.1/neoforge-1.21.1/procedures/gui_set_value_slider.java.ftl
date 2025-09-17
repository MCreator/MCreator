<#if w.hasElementsOfType("gui")>
<@head>if (${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {</@head>
	_menu.sendMenuStateUpdate(_player, 2, "${field$slider}", ${input$value}, true);
<@tail>}</@tail>
</#if>