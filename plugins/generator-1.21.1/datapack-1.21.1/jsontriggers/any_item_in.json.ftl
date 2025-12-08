<#include "mcitems.ftl">
<#if field_list$item?size == 1>
"items": "${mappedMCItemToRegistryName(w.itemBlock(field_list$item?first))}"
<#else>
"items": [
	<#list field_list$item as item>"${mappedMCItemToRegistryName(w.itemBlock(item))}"<#sep>,</#list>
]
</#if>