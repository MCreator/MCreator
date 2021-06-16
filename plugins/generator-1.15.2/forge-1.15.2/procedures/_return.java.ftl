<#include "mcitems.ftl">
return
<#if type == "itemstack">${mappedMCItemToItemStackCode(value, 1)}
<#elseif type == "blockstate">${mappedBlockToBlockStateCode(value)}
<#else>${value}</#if>;