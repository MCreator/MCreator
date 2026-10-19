<#include "../mcitems.ftl">
{
  "values": {
    <#list flammables as block>
    "${modid}:${block.getModElement().getRegistryName()}": {
      "burn_odds": ${block.flammability},
      "ignite_odds": ${block.fireSpreadSpeed}
    }<#sep>,</#list>
  }
}
