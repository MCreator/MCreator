<#include "../mcitems.ftl">
{
  "values": {
    <#list itemextensions?filter(e -> e.isCompostable()) as extension>
    "${mappedMCItemToRegistryName(extension.item)}": {
      "chance": <#if extension.compostLayerChance.entry()??>${extension.compostLayerChance.entry()}<#else>${extension.compostLayerChance.fixedValue()}</#if>
    }
    <#sep>,</#list>
  }
}