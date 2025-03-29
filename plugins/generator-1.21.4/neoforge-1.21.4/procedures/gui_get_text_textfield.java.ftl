<#if w.hasElementsOfType("gui")>
(${input$entity} instanceof Player _player ? (_player.containerMenu instanceof ${JavaModName}Menus.${JavaModName}MenuAccessor acc ? acc.getTextFieldValue("${field$textfield}") : ""): "")
<#else>""</#if>
