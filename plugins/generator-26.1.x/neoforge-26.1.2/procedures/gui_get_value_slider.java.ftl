<#if w.hasElementsOfType("gui")>
((${input$entity} instanceof Player _entity${cbi} && _entity${cbi}.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu${cbi}) ? _menu${cbi}.getMenuState(2, "${field$slider}", 0.0) : 0.0)
<#else>0</#if>