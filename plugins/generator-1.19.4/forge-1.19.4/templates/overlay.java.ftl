<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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

<#assign stackMethodName = "getPoseStack">

package ${package}.client.screens;

@Mod.EventBusSubscriber({Dist.CLIENT}) public class ${name}Overlay {

	@SubscribeEvent(priority = EventPriority.${data.priority})
	<#if generator.map(data.overlayTarget, "screens") == "Ingame">
        public static void eventHandler(RenderGuiEvent.Pre event) {
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
	<#else>
        public static void eventHandler(ScreenEvent.Render.Post event) {
            if (event.getScreen() instanceof ${generator.map(data.overlayTarget, "screens")}) {
                int w = event.getScreen().width;
                int h = event.getScreen().height;
	</#if>

        int posX = w / 2;
        int posY = h / 2;

        Level world = null;
        double x = 0;
        double y = 0;
        double z = 0;

        Player entity = Minecraft.getInstance().player;
        if (entity != null) {
            world = entity.level;
            x = entity.getX();
            y = entity.getY();
            z = entity.getZ();
        }

        <#if data.hasTextures()>
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
                RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/screens/${data.baseTexture}"));
                Minecraft.getInstance().gui.blit(event.getPoseStack(), 0, 0, 0, 0, w, h, w, h);
            </#if>

            <#list data.getComponentsOfType("Image") as component>
                <#assign x = component.x - 213>
                <#assign y = component.y - 120>
                <#if hasProcedure(component.displayCondition)>
                        if (<@procedureOBJToConditionCode component.displayCondition/>) {
                </#if>
                    RenderSystem.setShaderTexture(0, new ResourceLocation("${modid}:textures/screens/${component.image}"));
                    Minecraft.getInstance().gui.blit(event.getPoseStack(), posX + ${x}, posY + ${y}, 0, 0,
                        ${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
                        ${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
                <#if hasProcedure(component.displayCondition)>}</#if>
            </#list>

            <#list data.getComponentsOfType("Label") as component>
                    <#if hasProcedure(component.displayCondition)>
                        if (<@procedureOBJToConditionCode component.displayCondition/>)
                    </#if>
                    <#if component.anchorPoint.name() == "TOP_LEFT">
						posX = ${component.x};
						posY = ${component.y};
					<#elseif component.anchorPoint.name() == "TOP_RIGHT">
						posX = w - (427 - ${component.x});
						posY = ${component.y};
					<#elseif component.anchorPoint.name() == "BOTTOM_LEFT">
						posX = ${component.x};
						posY = h - (240 - ${component.y});
					<#elseif component.anchorPoint.name() == "BOTTOM_RIGHT">
						posX = w - (427 - ${component.x});
						posY = h - (240 - ${component.y});
					<#elseif component.anchorPoint.name() == "CENTER">
						posX = w / 2 + ${component.x - 213};
						posY = h / 2 + ${component.y - 120};
					</#if>
                    Minecraft.getInstance().font.draw(event.getPoseStack(),
                        <#if hasProcedure(component.text)><@procedureOBJToStringCode component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
                        posX, posY, ${component.color.getRGB()});
            </#list>

			posX = w / 2;
			posY = w / 2;
			<#list data.getComponentsOfType("EntityModel") as component>
			    if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
			    	<#if hasProcedure(component.displayCondition)>
                        if (<@procedureOBJToConditionCode component.displayCondition/>)
                    </#if>
			        InventoryScreen.renderEntityInInventoryFollowsAngle(event.getPoseStack(), posX + ${component.x - 202}, posY + ${component.y - 100},
                        ${component.scale}, ${component.rotationX / 20.0}f, 0, livingEntity);
			    }
			</#list>
        }

        <#if data.hasTextures()>
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        </#if>
    <#if generator.map(data.overlayTarget, "screens") != "Ingame">
        }
    </#if>
	}

}
<#-- @formatter:on -->