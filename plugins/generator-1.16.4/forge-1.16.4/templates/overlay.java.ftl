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
<#include "tokens.ftl">
<#include "procedures.java.ftl">

package ${package}.gui.overlay;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Overlay extends ${JavaModName}Elements.ModElement{

	public ${name}Overlay (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override
	public void initElements() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.${data.priority})
	public void eventHandler(RenderGameOverlayEvent event) {
		if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {

			int posX = (event.getWindow().getScaledWidth()) / 2;
			int posY = (event.getWindow().getScaledHeight()) / 2;

			PlayerEntity entity = Minecraft.getInstance().player;
			World world = entity.world;
			double x = entity.getPosX();
			double y = entity.getPosY();
			double z = entity.getPosZ();

			if (<@procedureOBJToConditionCode data.displayCondition/>) {
				<#if data.baseTexture?has_content>
					RenderSystem.disableDepthTest();
      				RenderSystem.depthMask(false);
      				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      				RenderSystem.disableAlphaTest();

					Minecraft.getInstance().getTextureManager()
								.bindTexture(new ResourceLocation("${modid}:textures/${data.baseTexture}"));
					Minecraft.getInstance().ingameGUI.blit(event.getMatrixStack(), 0, 0, 0, 0, event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight(),
							event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());

					RenderSystem.depthMask(true);
      				RenderSystem.enableDepthTest();
      				RenderSystem.enableAlphaTest();
      				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				</#if>

				<#list data.components as component>
	                <#assign x = component.x - 213>
	                <#assign y = component.y - 120>
	                <#if component.getClass().getSimpleName() == "Label">
						<#if hasCondition(component.displayCondition)>
						if (<@procedureOBJToConditionCode component.displayCondition/>)
						</#if>
						Minecraft.getInstance().fontRenderer.drawString(event.getMatrixStack(), "${translateTokens(JavaConventions.escapeStringForJava(component.text))}",
									posX + ${x}, posY + ${y}, ${component.color.getRGB()});
	                <#elseif component.getClass().getSimpleName() == "Image">
						<#if hasCondition(component.displayCondition)>
						if (<@procedureOBJToConditionCode component.displayCondition/>) {
						</#if>
						RenderSystem.disableDepthTest();
						RenderSystem.depthMask(false);
						RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						RenderSystem.disableAlphaTest();

						Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("${modid}:textures/${component.image}"));
						Minecraft.getInstance().ingameGUI.blit(event.getMatrixStack(), posX + ${x}, posY + ${y}, 0, 0,
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
							${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});

						RenderSystem.depthMask(true);
      					RenderSystem.enableDepthTest();
      					RenderSystem.enableAlphaTest();
      					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						<#if hasCondition(component.displayCondition)>
						}
						</#if>
	                </#if>
	            </#list>
			}
		}
	}

}
<#-- @formatter:on -->