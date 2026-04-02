<#list bescripts as script>
import "./${script.getModElement().getRegistryName()}.js"
</#list>