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
<#include "tokens.ftl">
<#include "procedures.java.ftl">

<#assign hasTextures = data.baseTexture?has_content>
<#list data.components as component>
	<#if component.getClass().getSimpleName() == "Image">
		<#assign hasTextures = true>
		<#break>
	</#if>
</#list>

package ${package}.client.gui;

@Mod.EventBusSubscriber({Dist.CLIENT}) public class ${name}Overlay {

	@SubscribeEvent(priority = EventPriority.${data.priority})
	<#if generator.map(data.overlayTarget, "screens") == "Ingame">
	public static void eventHandler(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			int w = event.getWindow().getGuiScaledWidth();
			int h = event.getWindow().getGuiScaledHeight();
	<#else>
	public static void eventHandler(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (event.getGui() instanceof ${generator.map(data.overlayTarget, "screens")}) {
			int w = event.getGui().width;
			int h = event.getGui().height;
	</#if>

			int posX = w / 2;
			int posY = h / 2;

			Level _world = null;
			double _x = 0;
			double _y = 0;
			double _z = 0;

			Player entity = Minecraft.getInstance().player;
			if (entity != null) {
				_world = entity.level;
				_x = entity.getX();
				_y = entity.getY();
				_z = entity.getZ();
			}

			Level world = _world;
			double x = _x;
			double y = _y;
			double z = _z;

			<#if hasTextures>
				RenderSystem.disableDepthTest();
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderSystem.setShaderColor(1, 1, 1, 1);
			</#if>

			if (<@procedureOBJToConditionCode data.displayCondition/>) {
				<#if data.baseTexture?has_content>
					RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/${data.baseTexture}"));
					Minecraft.getInstance().gui.blit(event.getMatrixStack(), 0, 0, 0, 0, w, h, w, h);
				</#if>

				<#list data.components as component>
	                <#assign x = component.x - 213>
	                <#assign y = component.y - 120>
	                <#if component.getClass().getSimpleName() == "Label">
						<#if hasProcedure(component.displayCondition)>
						if (<@procedureOBJToConditionCode component.displayCondition/>)
						</#if>
						Minecraft.getInstance().font.draw(event.getMatrixStack(), "${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
									posX + ${x}, posY + ${y}, ${component.color.getRGB()});
	                <#elseif component.getClass().getSimpleName() == "Image">
						<#if hasProcedure(component.displayCondition)>
						if (<@procedureOBJToConditionCode component.displayCondition/>) {
						</#if>
						RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/${component.image}"));
						Minecraft.getInstance().gui.blit(event.getMatrixStack(), posX + ${x}, posY + ${y}, 0, 0,
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});

						<#if hasProcedure(component.displayCondition)>}</#if>
	                </#if>
	            </#list>
			}

			<#if hasTextures>
				RenderSystem.depthMask(true);
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1, 1, 1, 1);
			</#if>
		}
	}

}
<#-- @formatter:on -->