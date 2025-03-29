<#if w.hasElementsOfType("gui")>
(${input$entity} instanceof Player _player ? (_player.containerMenu instanceof ${JavaModName}Menus.${JavaModName}MenuAccessor acc ? acc.getCheckBoxValue("${field$checkbox}") : false): false)
<#else>false</#if>