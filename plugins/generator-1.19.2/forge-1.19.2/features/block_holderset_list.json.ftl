<#include "mcitems.ftl">
<#if field_list$block?size == 1>
  "${mappedMCItemToIngameNameNoTags(w.itemBlock(field_list$block?first))}"
<#else>
  [
    <#list field_list$block as block>"${mappedMCItemToIngameNameNoTags(w.itemBlock(block))}"<#sep>,</#list>
  ]
</#if>