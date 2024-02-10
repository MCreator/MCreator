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
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

package ${package}.world.dimension;

<#compress>
@Mod.EventBusSubscriber public class ${name}Dimension {

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public static class DimensionSpecialEffectsHandler {

		@SubscribeEvent @OnlyIn(Dist.CLIENT) public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
			DimensionSpecialEffects customEffect = new DimensionSpecialEffects(
				<#if data.imitateOverworldBehaviour>DimensionSpecialEffects.OverworldEffects.CLOUD_LEVEL<#else>Float.NaN</#if>,
				true,
				<#if data.imitateOverworldBehaviour>DimensionSpecialEffects.SkyType.NORMAL<#else>DimensionSpecialEffects.SkyType.NONE</#if>,
				false,
				false
			) {
				@Override public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
					<#if data.airColor?has_content>
						return new Vec3(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
					<#else>
						<#if data.imitateOverworldBehaviour>
							return color.multiply(sunHeight * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight * 0.91 + 0.09);
						<#else>
							return color;
						</#if>
					</#if>
				}

				@Override public boolean isFoggyAt(int x, int y) {
					return ${data.hasFog};
				}
			};
			event.register(new ResourceLocation("${modid}:${registryname}"), customEffect);
		}

	}

	<#if hasProcedure(data.onPlayerLeavesDimension) || hasProcedure(data.onPlayerEntersDimension)>
	@SubscribeEvent public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		Entity entity = event.getEntity();
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		<#if hasProcedure(data.onPlayerLeavesDimension)>
		if (event.getFrom() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerLeavesDimension/>
		}
        </#if>

		<#if hasProcedure(data.onPlayerEntersDimension)>
		if (event.getTo() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("${modid}:${registryname}"))) {
			<@procedureOBJToCode data.onPlayerEntersDimension/>
		}
        </#if>
	}
    </#if>

}
</#compress>
