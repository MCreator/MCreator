<#include "mcitems.ftl">
${outputMarker}(${condition} ?
<#if outputMarker == "/*@ItemStack*/">${mappedMCItemToItemStackCode(ifTrue, 1)}
<#elseif outputMarker == "/*@BlockState*/">${mappedBlockToBlockStateCode(ifTrue)}
<#else>${ifTrue}</#if> :
<#if outputMarker == "/*@ItemStack*/">${mappedMCItemToItemStackCode(ifFalse, 1)}
<#elseif outputMarker == "/*@BlockState*/">${mappedBlockToBlockStateCode(ifFalse)}
<#else>${ifFalse}</#if>)