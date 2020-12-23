<#include "mcitems.ftl">
return
<#if type == "return_itemstack">
    ${mappedMCItemToItemStackCode(value, 1)}
<#elseif type == "return_blockstate">
    ${mappedBlockToBlockStateCode(value)}
<#else>
    ${value}
</#if>;