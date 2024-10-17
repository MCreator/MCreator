<#include "../mcitems.ftl">
{
  "values": {
    <#list itemextensions?filter(e -> e.compostLayerChance gt 0) as extension>
    "${mappedMCItemToRegistryName(extension.item)}": {
      "chance": ${extension.compostLayerChance}
    }
    <#sep>,</#list>
  }
}