<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.util;

@Elements${JavaModName}.ModElement.Tag public class OreDict${name} extends Elements${JavaModName}.ModElement {

	public OreDict${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void init(FMLInitializationEvent event){
		<#if data.type == "Items">
			<#list data.items as value>
			OreDictionary.registerOre("${data.getOreDictName()}", ${mappedMCItemToItemStackCode(value, 1)});
			</#list>
		<#elseif data.type == "Blocks">
			<#list data.blocks as value>
			OreDictionary.registerOre("${data.getOreDictName()}", ${mappedMCItemToItemStackCode(value, 1)});
			</#list>
		</#if>
	}

}
<#-- @formatter:on -->