<#include "mcitems.ftl">
<#list field_list$block as block>${mappedBlockToBlock(toMappedMCItem(block))}<#sep>,</#list>