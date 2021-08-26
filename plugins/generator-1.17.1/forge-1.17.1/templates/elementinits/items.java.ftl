<#-- @formatter:off -->

<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Items {

    private static final List<Item> REGISTRY = new ArrayList();

    <#list items as item>
    public static Item ${item.getModElement().geRegistryNameUpper()} = register(new ${item.getModElement().getName()}Item());
    </#list>

    private static Item register(Item item) {
		REGISTRY.add(item);
    	return item;
    }

	@SubscribeEvent public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new Item[0]));
	}

}

<#-- @formatter:on -->