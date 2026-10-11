<#include "mcitems.ftl">
/*@int*/(world instanceof ServerLevel _levelFV${cbi} ?
		ResolvableInt.getFromItem(${mappedMCItemToItemStackCode(input$item, 1)}, DataComponents.COOKING_FUEL, CookingFuel::burnTime,
			new LootContext.Builder(new LootParams.Builder(_levelFV${cbi}).create(LootContextParamSets.EMPTY))
				.create(Optional.empty()), 0) : 0)