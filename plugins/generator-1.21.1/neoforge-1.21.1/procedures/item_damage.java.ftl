<#include "mcitems.ftl">
<@head>if (world instanceof ServerLevel _level) {</@head>
	${mappedMCItemToItemStackCode(input$item, 1)}.hurtAndBreak(${opt.toInt(input$amount)}, _level, null, _stkprov -> {});
<@tail>}</@tail>