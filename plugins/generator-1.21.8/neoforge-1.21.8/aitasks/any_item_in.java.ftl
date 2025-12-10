<#include "mcitems.ftl">
Ingredient.of(<#list field_list$item as item>${mappedMCItemToItem(w.itemBlock(item))}<#sep>,</#list>)