import "./${modid}_variables.js"

<#list w.getElementsOfType("bescript") as script>
import "./${script.getRegistryName()}.js"
</#list>