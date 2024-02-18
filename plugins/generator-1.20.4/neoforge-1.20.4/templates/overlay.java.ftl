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
<#include "procedures.java.ftl">

package ${package}.client.screens;

<#assign hasEntityModels = false>

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

        Level world = null;
        double x = 0;
        double y = 0;
        double z = 0;

        Player entity = Minecraft.getInstance().player;
        if (entity != null) {
            world = entity.level();
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
                event.getGuiGraphics().blit(new ResourceLocation("${modid}:textures/screens/${data.baseTexture}"), 0, 0, 0, 0, w, h, w, h);
            </#if>

            <#list data.getComponentsOfType("Image") as component>
                <#if hasProcedure(component.displayCondition)>
                        if (<@procedureOBJToConditionCode component.displayCondition/>) {
                </#if>
                    event.getGuiGraphics().blit(new ResourceLocation("${modid}:textures/screens/${component.image}"), <@calculatePosition component/>, 0, 0,
                        ${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())},
                        ${component.getWidth(w.getWorkspace())}, ${component.getHeight(w.getWorkspace())});
                <#if hasProcedure(component.displayCondition)>}</#if>
            </#list>

            <#list data.getComponentsOfType("Label") as component>
                <#if hasProcedure(component.displayCondition)>
                    if (<@procedureOBJToConditionCode component.displayCondition/>)
                </#if>
                event.getGuiGraphics().drawString(Minecraft.getInstance().font,
                    <#if hasProcedure(component.text)><@procedureOBJToStringCode component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
                    <@calculatePosition component/>, ${component.color.getRGB()}, false);
            </#list>

			<#list data.getComponentsOfType("EntityModel") as component>
				<#assign hasEntityModels = true>
			    if (<@procedureOBJToConditionCode component.entityModel/> instanceof LivingEntity livingEntity) {
			    	<#if hasProcedure(component.displayCondition)>
                        if (<@procedureOBJToConditionCode component.displayCondition/>)
                    </#if>
					renderEntityInInventoryFollowsAngle(event.getGuiGraphics(), <@calculatePosition component=component x_offset=10 y_offset=20/>,
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

	<#if hasEntityModels>
	private static void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int x, int y, int scale, float angleXComponent, float angleYComponent, LivingEntity entity) {
		Quaternionf pose = new Quaternionf().rotateZ((float)Math.PI);
		Quaternionf cameraOrientation = new Quaternionf().rotateX(angleYComponent * 20 * ((float) Math.PI / 180F));
		pose.mul(cameraOrientation);
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
		entity.setYRot(180.0F + angleXComponent * 40.0F);
		entity.setXRot(-angleYComponent * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		InventoryScreen.renderEntityInInventory(guiGraphics, x, y, scale, new Vector3f(0, 0, 0), pose, cameraOrientation, entity);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
	}
	</#if>

}

<#macro calculatePosition component x_offset=0 y_offset=0>
	<#if component.anchorPoint.name() == "TOP_LEFT">
		${component.x + x_offset}, ${component.y + y_offset}
	<#elseif component.anchorPoint.name() == "TOP_CENTER">
		w / 2 + ${component.x - (213 - x_offset)}, ${component.y + y_offset}
	<#elseif component.anchorPoint.name() == "TOP_RIGHT">
		w - ${427 - (component.x + x_offset)}, ${component.y + y_offset}
	<#elseif component.anchorPoint.name() == "CENTER_LEFT">
		${component.x + x_offset}, h / 2 + ${component.y - (120 - y_offset)}
	<#elseif component.anchorPoint.name() == "CENTER">
		w / 2 + ${component.x - (213 - x_offset)}, h / 2 + ${component.y - (120 - y_offset)}
	<#elseif component.anchorPoint.name() == "CENTER_RIGHT">
		w - ${427 - (component.x + x_offset)}, h / 2 + ${component.y - (120 - y_offset)}
	<#elseif component.anchorPoint.name() == "BOTTOM_LEFT">
		${component.x + x_offset}, h - ${240 - (component.y + y_offset)}
	<#elseif component.anchorPoint.name() == "BOTTOM_CENTER">
		w / 2 + ${component.x - (213 - x_offset)}, h - ${240 - (component.y + y_offset)}
	<#elseif component.anchorPoint.name() == "BOTTOM_RIGHT">
		w - ${427 - (component.x + x_offset)}, h - ${240 - (component.y + y_offset)}
	</#if>
</#macro>
<#-- @formatter:on -->