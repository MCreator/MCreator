<#-- @formatter:off -->

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class Paintings {

	@SubscribeEvent public static void registerMotives(RegistryEvent.Register<Motive> event) {
		<#list paintings as painting>
		event.getRegistry().register(new Motive(${painting.width}, ${painting.height}).setRegistryName("${painting.getModElement().getRegistryName()}"));
        </#list>
	}

}

<#-- @formatter:on -->