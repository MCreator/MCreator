<#include "mcitems.ftl">
return
<#if type == "itemstack">${opt.removeParentheses(mappedMCItemToItemStackCode(value, 1))}
<#elseif type == "blockstate">${opt.removeParentheses(mappedBlockToBlockStateCode(value))}
<#else>${opt.removeParentheses(value)}</#if>;