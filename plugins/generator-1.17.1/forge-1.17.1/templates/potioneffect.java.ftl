<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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
<#include "mcitems.ftl">
<#include "procedures.java.ftl">

package ${package}.potion;

public class ${name}MobEffect extends MobEffect {

	public ${name}MobEffect() {
		super(MobEffectCategory.<#if data.isBad>HARMFUL<#elseif data.isBenefitical>BENEFICIAL<#else>NEUTRAL</#if>, ${data.color.getRGB()});
		setRegistryName("${registryname}");
	}

	@Override public String getDescriptionId() {
		return "effect.${modid}.${registryname}";
	}

	<#if data.isInstant>
	@Override public boolean isInstantenous() {
	   	return true;
   	}
   	</#if>

	<#if hasProcedure(data.onStarted)>
		<#if data.isInstant>
		@Override public void applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health) {
			Level world = entity.level;
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			<@procedureOBJToCode data.onStarted/>
		}
		<#else>
		@Override public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
			Level world = entity.level;
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			<@procedureOBJToCode data.onStarted/>
		}
		</#if>
	</#if>

	<#if hasProcedure(data.onActiveTick)>
	@Override public void applyEffectTick(LivingEntity entity, int amplifier) {
		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onActiveTick/>
	}
	</#if>

   	<#if hasProcedure(data.onExpired)>
	@Override public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
   		super.removeAttributeModifiers(entity, attributeMap, amplifier);
   		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		<@procedureOBJToCode data.onExpired/>
	}
	</#if>

	@Override public boolean isDurationEffectTick(int duration, int amplifier) {
		<#if hasProcedure(data.activeTickCondition)>
		return <@procedureOBJToConditionCode data.activeTickCondition/>;
		<#else>
		return true;
		</#if>
	}

	<#if data.hasCustomRenderer()>
	@Override public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.EffectRenderer> consumer) {
		consumer.accept(new EffectRenderer() {
			<#if !data.renderStatusInInventory>
			@Override public boolean shouldRender(MobEffectInstance effect) {
				return false;
			}

			@Override public boolean shouldRenderInvText(MobEffectInstance effect) {
				return false;
			}
			</#if>

			<#if !data.renderStatusInHUD>
			@Override public boolean shouldRenderHUD(MobEffectInstance effect) {
				return false;
			}
			</#if>

			@Override public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {}

			@Override public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {}
		});
	}
	</#if>
}
<#-- @formatter:on -->