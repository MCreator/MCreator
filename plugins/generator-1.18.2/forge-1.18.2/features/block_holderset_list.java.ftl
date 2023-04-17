<#include "mcitems.ftl">
<#list field_list$block as block>${mappedBlockToBlock(w.itemBlock(block))}<#sep>,</#list>