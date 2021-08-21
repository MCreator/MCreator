<#-- @formatter:off -->

<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign fuels = []>
<#list w.getElementsOfType("fuel") as fuel>
    <#assign fuels += [fuel.getGeneratableElement()]>
</#list>

@Mod.EventBusSubscriber public class FuelRegistry {

	@SubscribeEvent
	public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
		<#list fuels as fuel>
		<#if fuel?index == 0>if<#else>else if</#if>(event.getItemStack().getItem() == ${mappedMCItemToItem(fuel.block)})
			event.setBurnTime(${fuel.power});
		</#list>
	}

}

<#-- @formatter:on -->