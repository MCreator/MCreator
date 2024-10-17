<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.item.crafting;

@Elements${JavaModName}.ModElement.Tag public class Recipe${name} extends Elements${JavaModName}.ModElement{

	public Recipe${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void init(FMLInitializationEvent event) {
		GameRegistry.addSmelting(${mappedMCItemToItemStackCode(data.smeltingInputStack, 1)},
    		${mappedMCItemToItemStackCode(data.smeltingReturnStack, data.recipeRetstackSize)},${data.xpReward}F);
	}

}
<#-- @formatter:on -->