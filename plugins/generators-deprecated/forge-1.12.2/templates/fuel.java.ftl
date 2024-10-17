<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.fuel;

@Elements${JavaModName}.ModElement.Tag public class Fuel${name} extends Elements${JavaModName}.ModElement {

	public Fuel${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public int addFuel(ItemStack fuel) {
		<#if !data.block.toString().contains("#")>
		if(fuel.getItem() == ${mappedMCItemToItem(data.block)})
			return ${data.power};
        <#else>
		if(fuel.getItem() == ${mappedMCItemToItem(data.block)} && fuel.getMetadata()== ${mappedMCItemToItemStackCode(data.block, 1)}.getMetadata())
			return ${data.power};
        </#if>
		return 0;
	}

}
<#-- @formatter:on -->