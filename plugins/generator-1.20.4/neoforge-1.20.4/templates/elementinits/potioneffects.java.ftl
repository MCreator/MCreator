<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
<#include "../procedures.java.ftl">

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

<#assign effects_that_expire = potioneffects?filter(effect -> hasProcedure(effect.onExpired))>

<#if effects_that_expire?size != 0>@Mod.EventBusSubscriber </#if>public class ${JavaModName}MobEffects {

	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, ${JavaModName}.MODID);

	<#list potioneffects as effect>
	public static final DeferredHolder<MobEffect, MobEffect> ${effect.getModElement().getRegistryNameUpper()} =
			REGISTRY.register("${effect.getModElement().getRegistryName()}", () -> new ${effect.getModElement().getName()}MobEffect());
	</#list>

	<#if effects_that_expire?size != 0>
	@SubscribeEvent public static void onEffectRemoved(MobEffectEvent.Remove event) {
		MobEffectInstance effectInstance = event.getEffectInstance();
		if (effectInstance != null) {
			expireEffects(event.getEntity(), effectInstance);
		}
	}

	@SubscribeEvent public static void onEffectExpired(MobEffectEvent.Expired event) {
		MobEffectInstance effectInstance = event.getEffectInstance();
		if (effectInstance != null) {
			expireEffects(event.getEntity(), effectInstance);
		}
	}

	private static void expireEffects(Entity entity, MobEffectInstance effectInstance) {
		<#compress>
		MobEffect effect = effectInstance.getEffect();
		<#list effects_that_expire as effect>
		if (effect == ${effect.getModElement().getRegistryNameUpper()}.get()) {
			<@procedureCode effect.onExpired, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "entity.level()",
				"entity": "entity",
				"amplifier": "effectInstance.getAmplifier()"
			}/>
		}<#sep>else
		</#list>
		</#compress>
	}
	</#if>
}

<#-- @formatter:on -->