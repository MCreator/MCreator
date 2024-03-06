<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

<#compress>
public class ${name}MobEffect extends MobEffect {

	public ${name}MobEffect() {
		super(MobEffectCategory.<#if data.isBad>HARMFUL<#elseif data.isBenefitical>BENEFICIAL<#else>NEUTRAL</#if>, ${data.color.getRGB()});
	}

	<#if data.isInstant>
		@Override public boolean isInstantenous() {
			return true;
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
			@Override public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
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

	<#if hasProcedure(data.onActiveTick)>
		@Override public void applyEffectTick(LivingEntity entity, int amplifier) {
			<@procedureCode data.onActiveTick, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "entity.level()",
				"entity": "entity",
				"amplifier": "amplifier"
			}/>
		}
	</#if>

	<#if hasProcedure(data.onExpired)>
		@Override public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
			super.removeAttributeModifiers(entity, attributeMap, amplifier);
			<@procedureCode data.onExpired, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "entity.level()",
				"entity": "entity",
				"amplifier": "amplifier"
			}/>
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
		@Override public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
			consumer.accept(new IClientMobEffectExtensions() {
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
			});
		}
	</#if>
}
</#compress>
<#-- @formatter:on -->