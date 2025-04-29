<#if w.hasElementsOfType("gui")>
((${input$entity} instanceof Player _entity && _entity.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) ? _menu.getMenuState(0, "${field$textfield}", "") : "")
<#else>""</#if>