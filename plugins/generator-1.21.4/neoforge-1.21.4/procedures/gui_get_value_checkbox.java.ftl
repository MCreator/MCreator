<#if w.hasElementsOfType("gui")>
((${input$entity} instanceof Player _entity && _entity.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) ? _menu.getMenuState(1, "${field$checkbox}", false) : false)
<#else>false</#if>