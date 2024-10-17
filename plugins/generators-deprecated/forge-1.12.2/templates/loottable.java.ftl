<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.util;

@Elements${JavaModName}.ModElement.Tag public class LootTable${name} extends Elements${JavaModName}.ModElement {

	public LootTable${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void init(FMLInitializationEvent event) {
		LootTableList.register(new ResourceLocation("${data.getNamespace()}", "${data.getName()}"));
	}

}
<#-- @formatter:on -->