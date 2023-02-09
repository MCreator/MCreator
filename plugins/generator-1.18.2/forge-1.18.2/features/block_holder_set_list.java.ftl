<#include "mcitems.ftl">
HolderSet.direct(Block::builtInRegistryHolder<#list field_list$block as block>, ${mappedBlockToBlock(toMappedMCItem(block))}</#list>)