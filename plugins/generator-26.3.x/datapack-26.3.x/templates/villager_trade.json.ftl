<#-- @formatter:off -->
<#include "mcitems_json.ftl">
{
  "wants": {
    ${mappedMCItemToItemObjectJSON(data.price1, "id")}
    <#if data.countPrice1 != 1>, "count": ${data.countPrice1}</#if>
  },
  <#if !data.price2.isEmpty()>
  "additional_wants": {
    ${mappedMCItemToItemObjectJSON(data.price2, "id")}
    <#if data.countPrice2 != 1>, "count": ${data.countPrice2}</#if>
  },</#if>
  "gives": {
    ${mappedMCItemToItemObjectJSON(data.offer, "id")}
    <#if data.countOffer != 1>, "count": ${data.countOffer}</#if>
  },
  "max_uses": ${data.maxTrades},
  "xp": ${data.xp},
  "reputation_discount": ${data.priceMultiplier}
}
<#-- @formatter:on -->