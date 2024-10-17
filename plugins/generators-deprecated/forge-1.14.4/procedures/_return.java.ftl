<#include "mcitems.ftl">
return <#if type == "return_itemstack">${mappedMCItemToItemStackCode(value, 1)}<#else>${value}</#if>;