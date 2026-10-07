<#if w.hasElementsOfType("gui")>
((${input$entity} instanceof Player _entity${cbi} && _entity${cbi}.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu${cbi}) ? _menu${cbi}.getMenuState(0, "${field$textfield}", "") : "")
<#else>""</#if>