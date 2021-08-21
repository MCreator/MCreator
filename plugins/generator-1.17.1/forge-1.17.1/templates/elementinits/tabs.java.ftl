<#-- @formatter:off -->

<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign tabs = []>
<#list w.getElementsOfType("tab") as tab>
    <#assign tabs += [tab.getGeneratableElement()]>
</#list>

public class CreativeModeTabs {

    <#list tabs as tab>
    public static CreativeModeTab TAB_${tab.getModElement().getRegistryName()?upper_case};
    </#list>

	public static void load() {
        <#list tabs as tab>
        TAB_${tab.getModElement().getRegistryName()?upper_case} = new CreativeModeTab("tab${tab.getModElement().getRegistryName()}") {
			@Override public ItemStack makeIcon() {
				return ${mappedMCItemToItemStackCode(tab.icon, 1)};
			}

			@OnlyIn(Dist.CLIENT) public boolean hasSearchBar() {
				return ${tab.showSearch};
			}
        }<#if tab.showSearch>.setBackgroundSuffix("item_search.png")</#if>;
        </#list>
    }

}

<#-- @formatter:on -->