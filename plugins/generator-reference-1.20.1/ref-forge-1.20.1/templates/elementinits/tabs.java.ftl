<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
 #
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 #
 # Additional permission for code generator templates (*.ftl files)
 #
 # As a special exception, you may create a larger work that contains part or
 # all of the MCreator code generator templates (*.ftl files) and distribute
 # that work under terms of your choice, so long as that work isn't itself a
 # template for code generation. Alternatively, if you modify or redistribute
 # the template itself, you may (at your option) remove this special exception,
 # which will cause the template and the resulting code generator output files
 # to be licensed under the GNU General Public License without this special
 # exception.
-->

<#-- @formatter:off -->

<#include "../mcitems.ftl">

<#assign tabMap = w.getCreativeTabMap()>
<#assign vanillaTabs = tabMap.keySet()?filter(e -> !e?starts_with('CUSTOM:'))>
<#assign customTabs = tabMap.keySet()?filter(e -> e?starts_with('CUSTOM:'))>

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#if vanillaTabs?has_content>
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
</#if>
public class ${JavaModName}Tabs {

	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ${JavaModName}.MODID);

	<#list customTabs as customTab>
		<#assign tab = w.getWorkspace().getModElementByName(customTab.replace("CUSTOM:", "")).getGeneratableElement()>
			public static final RegistryObject<CreativeModeTab> ${tab.getModElement().getRegistryNameUpper()} =
				REGISTRY.register("${tab.getModElement().getRegistryName()}", () ->
					CreativeModeTab.builder()
						.title(Component.translatable("item_group.${modid}.${tab.getModElement().getRegistryName()}"))
						.icon(() -> ${mappedMCItemToItemStackCode(tab.icon, 1)})
						.displayItems((parameters, tabData) -> {
							<#list tabMap.get("CUSTOM:" + tab.getModElement().getName()) as tabElement>
							tabData.accept(${mappedMCItemToItem(tabElement)});
							</#list>
						})
						<#if tab.showSearch>.withSearchBar()</#if>
						.build()
				);
	</#list>

	<#if vanillaTabs?has_content>
	@SubscribeEvent public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		<#list vanillaTabs as tabName>
			<#if !tabName?is_first>else </#if>if (tabData.getTabKey() == ${generator.map(tabName, "tabs")}) {
				<#list tabMap.get(tabName) as tabElement>
				tabData.accept(${mappedMCItemToItem(tabElement)});
				</#list>
			}
		</#list>
	}
	</#if>

}

<#-- @formatter:on -->