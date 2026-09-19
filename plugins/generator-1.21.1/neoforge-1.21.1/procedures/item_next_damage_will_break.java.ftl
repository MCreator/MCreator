<#include "mcitems.ftl">
<#assign itemStack = mappedMCItemToItemStackCode(input$item, 1)>
(${itemStack}.isDamageableItem() && ${itemStack}.getDamageValue() >= ${itemStack}.getMaxDamage() - 1)