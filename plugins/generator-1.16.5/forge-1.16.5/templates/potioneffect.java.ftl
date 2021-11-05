<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)  public class ${name}PotionEffect {

	@ObjectHolder("${modid}:${registryname}")
	public static final Effect potion = null;

	@SubscribeEvent public static void registerEffect(RegistryEvent.Register<Effect> event) {
		event.getRegistry().register(new EffectCustom());
	}

	public static class EffectCustom extends Effect {

		public EffectCustom() {
			super(EffectType.<#if data.isBad>HARMFUL<#elseif data.isBenefitical>BENEFICIAL<#else>NEUTRAL</#if>, ${data.color.getRGB()});
			setRegistryName("${registryname}");
		}

		@Override public String getName() {
      		return "effect.${registryname}";
   		}

		@Override public boolean isBeneficial() {
        	return ${data.isBenefitical};
    	}

		@Override public boolean isInstant() {
        	return ${data.isInstant};
    	}

   	 	@Override public boolean shouldRenderInvText(EffectInstance effect) {
    	    return ${data.renderStatusInInventory};
    	}

		@Override public boolean shouldRender(EffectInstance effect) {
			return ${data.renderStatusInInventory};
		}

    	@Override public boolean shouldRenderHUD(EffectInstance effect) {
    	    return ${data.renderStatusInHUD};
    	}

		<#if hasProcedure(data.onStarted)>
			<#if data.isInstant>
			@Override public void affectEntity(Entity source, Entity indirectSource, LivingEntity entity, int amplifier, double health) {
				World world = entity.world;
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				<@procedureOBJToCode data.onStarted/>
			}
			<#else>
			@Override public void applyAttributesModifiersToEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
				World world = entity.world;
				double x = entity.getPosX();
				double y = entity.getPosY();
				double z = entity.getPosZ();
				<@procedureOBJToCode data.onStarted/>
			}
			</#if>
		</#if>

		<#if hasProcedure(data.onActiveTick)>
		@Override public void performEffect(LivingEntity entity, int amplifier) {
			World world = entity.world;
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onActiveTick/>
		}
		</#if>

    	<#if hasProcedure(data.onExpired)>
		@Override public void removeAttributesModifiersFromEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
    		super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
    		World world = entity.world;
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();
			<@procedureOBJToCode data.onExpired/>
		}
		</#if>

		@Override public boolean isReady(int duration, int amplifier) {
			<#if hasProcedure(data.activeTickCondition)>
			return <@procedureOBJToConditionCode data.activeTickCondition/>;
			<#else>
			return true;
			</#if>
		}

	}

}
<#-- @formatter:on -->