<#include "../mcitems.ftl">
{
  "values": {
    <#list itemextensions?filter(e -> e.isCompostable()) as extension>
    "${mappedMCItemToRegistryName(extension.item)}": {
      "chance": <#if extension.compostableNumberProvider??>${extension.compostableNumberProvider}<#else>${extension.compostLayerChance}</#if>
    }
    <#sep>,</#list>
  }
}