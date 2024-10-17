<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.creativetab;

@Elements${JavaModName}.ModElement.Tag public class Tab${name} extends Elements${JavaModName}.ModElement{

	public Tab${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		tab = new CreativeTabs("tab${registryname}" ) {
			@SideOnly(Side.CLIENT) @Override public ItemStack getTabIconItem() {
				return ${mappedMCItemToItemStackCode(data.icon, 1)};
			}
			@SideOnly(Side.CLIENT) public boolean hasSearchBar() {
				return ${data.showSearch};
			}
		}<#if data.showSearch>.setBackgroundImageName("item_search.png")</#if>;
	}

	public static CreativeTabs tab;

}
<#-- @formatter:on -->