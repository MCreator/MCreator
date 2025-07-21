<#include "mcitems.ftl">
{
  "Name": "${mappedMCItemToRegistryName(w.itemBlock(field$block))}"
  <#if field_list$property?size != 0>,
  "Properties": {
    <#list 0..field_list$property?size-1 as i>
    "${field_list$property[i]}": "${field_list$value[i]}"
    <#sep>,</#list>
  }
  </#if>
}