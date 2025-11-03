<#include "mcitems.ftl">
<#list field_list$item as item>"${mappedMCItemToRegistryName(w.itemBlock(item))}"<#sep>,</#list>