<#include "mcitems.ftl">
/*@int*/(world instanceof Level _levelFV${cbi} ? ${mappedMCItemToItemStackCode(input$item, 1)}.getBurnTime(null, _levelFV${cbi}.fuelValues()) : 0)