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
<#include "procedures.java.ftl">

package ${package}.potion;

<#compress>
<#if data.hasCustomRenderer()>
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
</#if>
public class ${name}MobEffect extends <#if data.isInstant>Instantenous</#if>MobEffect {

	public ${name}MobEffect() {
		super(MobEffectCategory.${data.mobEffectCategory}, ${data.color.getRGB()});
		<#if data.onAddedSound?has_content && data.onAddedSound.getMappedValue()?has_content>
		this.withSoundOnAdded(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("${data.onAddedSound}")));
		</#if>
		<#list data.modifiers as modifier>
		this.addAttributeModifier(${modifier.attribute},
				ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "effect.${data.getModElement().getRegistryName()}_${modifier?index}"),
				${modifier.amount}, AttributeModifier.Operation.${modifier.operation});
		</#list>
	}

	<#if data.hasCustomParticle()>
	@Override public ParticleOptions createParticleOptions(MobEffectInstance mobEffectInstance) {
		return ${data.particle};
	}
	</#if>

	<#if data.isCuredbyHoney>
	@Override public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		cures.add(EffectCures.MILK);
		cures.add(EffectCures.PROTECTED_BY_TOTEM);
		cures.add(EffectCures.HONEY);
	}
	</#if>

	<#if hasProcedure(data.onStarted)>
		<#if data.isInstant>
			@Override public void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health) {
				<@procedureCode data.onStarted, {
					"x": "entity.getX()",
					"y": "entity.getY()",
					"z": "entity.getZ()",
					"world": "entity.level()",
					"entity": "entity",
					"amplifier": "amplifier"
				}/>
			}
		<#else>
			@Override public void onEffectStarted(LivingEntity entity, int amplifier) {
				<@procedureCode data.onStarted, {
					"x": "entity.getX()",
					"y": "entity.getY()",
					"z": "entity.getZ()",
					"world": "entity.level()",
					"entity": "entity",
					"amplifier": "amplifier"
				}/>
			}
		</#if>
	</#if>

	<#if hasProcedure(data.activeTickCondition) || hasProcedure(data.onActiveTick)>
		@Override public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
			<#if hasProcedure(data.activeTickCondition)>
				return <@procedureOBJToConditionCode data.activeTickCondition/>;
			<#else>
				return true;
			</#if>
		}
	</#if>

	<#if hasProcedure(data.onActiveTick)>
		@Override public boolean applyEffectTick(LivingEntity entity, int amplifier) {
			<@procedureCode data.onActiveTick, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "entity.level()",
				"entity": "entity",
				"amplifier": "amplifier"
			}/>
			return super.applyEffectTick(entity, amplifier);
		}
	</#if>

	<#if hasProcedure(data.onMobHurt)>
		@Override public void onMobHurt(LivingEntity entity, int amplifier, DamageSource damagesource, float damage) {
			<@procedureCode data.onMobHurt, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "entity.level()",
				"entity": "entity",
				"amplifier": "amplifier",
				"damagesource": "damagesource",
				"damage": "damage"
			}/>
		}
	</#if>

	<#if hasProcedure(data.onMobRemoved)>
		@Override public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
			if (reason == Entity.RemovalReason.KILLED) {
				<@procedureCode data.onMobRemoved, {
					"x": "entity.getX()",
					"y": "entity.getY()",
					"z": "entity.getZ()",
					"world": "entity.level()",
					"entity": "entity",
					"amplifier": "amplifier"
				}/>
			}
		}
	</#if>

	<#if data.hasCustomRenderer()>
	@SubscribeEvent public static void registerMobEffectExtensions(RegisterClientExtensionsEvent event) {
		event.registerMobEffect(new IClientMobEffectExtensions() {
			<#if !data.renderStatusInInventory>
			@Override public boolean isVisibleInInventory(MobEffectInstance effect) {
				return false;
			}

			@Override public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
				return false;
			}
			</#if>

			<#if !data.renderStatusInHUD>
			@Override public boolean isVisibleInGui(MobEffectInstance effect) {
				return false;
			}
			</#if>
		}, ${JavaModName}MobEffects.${REGISTRYNAME}.get());
	}
	</#if>
}
</#compress>
<#-- @formatter:on -->