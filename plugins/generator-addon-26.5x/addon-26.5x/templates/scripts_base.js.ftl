<#if w.hasVariables()>
import "./${modid}_variables.js"
</#if>

<#list w.getElementsOfType("bescript") as script>
import "./${script.getRegistryName()}.js"
</#list>