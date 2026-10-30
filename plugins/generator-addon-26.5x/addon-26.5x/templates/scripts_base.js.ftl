<#if w.hasVariables()>
import "./${modid}_variables.js"
</#if>

<#list types.bescripts as script>
import "./${script.getModElement().getRegistryName()}.js"
</#list>