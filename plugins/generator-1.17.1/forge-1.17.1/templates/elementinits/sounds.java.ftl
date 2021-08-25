<#-- @formatter:off -->

<#include "../mcitems.ftl">

/*
 *    MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

import net.minecraft.sounds.SoundEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${JavaModName}Sounds {

	public static Map<ResourceLocation, SoundEvent> REGISTRY = new HashMap<>();

	static {
		<#list sounds as sound>
		REGISTRY.put(new ResourceLocation("${modid}" ,"${sound}"), new SoundEvent(new ResourceLocation("${modid}", "${sound}")));
		</#list>
	}

	@SubscribeEvent public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for (Map.Entry<ResourceLocation, SoundEvent> sound : REGISTRY.entrySet())
			event.getRegistry().register(sound.getValue().setRegistryName(sound.getKey()));
	}

}

<#-- @formatter:on -->