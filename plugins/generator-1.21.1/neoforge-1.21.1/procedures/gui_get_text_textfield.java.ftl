<#if w.hasElementsOfType("gui")>
(entity instanceof Player _player ? (_player.containerMenu instanceof ${JavaModName}Menus.${JavaModName}MenuAccessor acc ? acc.getTextFieldsContent().getOrDefault("${field$textfield}", "") : ""): "")
<#else>""</#if>
