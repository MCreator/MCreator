<#include "mcitems.ftl">
<#if field_list$block?size == 1>
  "${mappedMCItemToRegistryName(w.itemBlock(field_list$block?first))}"
<#else>
  [
    <#list field_list$block as block>"${mappedMCItemToRegistryName(w.itemBlock(block))}"<#sep>,</#list>
  ]
</#if>