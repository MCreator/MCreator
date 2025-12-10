<#include "mcitems.ftl">
<#list field_list$item as item>${mappedMCItemToItem(w.itemBlock(item))}<#sep>,</#list>