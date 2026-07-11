<#include "mcitems.ftl">
if (world instanceof ServerLevel _level) {
	${mappedMCItemToItemStackCode(input$item, 1)}.hurtAndBreak(${opt.toInt(input$amount)}, _level, null, _stkprov -> {});
}