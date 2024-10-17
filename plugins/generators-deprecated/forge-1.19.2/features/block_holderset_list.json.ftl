<#include "mcitems.ftl">
<#if field_list$block?size == 1>
  "${mappedMCItemToIngameNameNoTags(toMappedMCItem(field_list$block?first))}"
<#else>
  [
    <#list field_list$block as block>"${mappedMCItemToIngameNameNoTags(toMappedMCItem(block))}"<#sep>,</#list>
  ]
</#if>