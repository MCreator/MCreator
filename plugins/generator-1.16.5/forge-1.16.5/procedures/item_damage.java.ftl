<#include "mcitems.ftl">
{
	ItemStack _ist = ${mappedMCItemToItemStackCode(input$item, 1)};
	if(_ist.attemptDamageItem((int) ${input$amount},new Random(),null)) {
        _ist.shrink(1);
        _ist.setDamage(0);
    }
}