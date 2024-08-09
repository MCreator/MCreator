<#include "mcitems.ftl">
{
	ItemStack _ist = ${mappedMCItemToItemStackCode(input$item, 1)};
	_ist.hurtAndBreak(${opt.toInt(input$amount)}, RandomSource.create(), null, () -> {
		_ist.shrink(1);
		_ist.setDamageValue(0);
	});
}