<#include "mcitems.ftl">
{
	ItemStack _ist = ${mappedMCItemToItemStackCode(input$item, 1)};
	if(_ist.hurt(${opt.toInt(input$amount)}, RandomSource.create(), null)) {
        _ist.shrink(1);
        _ist.setDamageValue(0);
    }
}