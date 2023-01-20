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
				<#assign x = component.x - 213>
				<#assign y = component.y - 120>
					<#if hasProcedure(component.displayCondition)>
						if (<@procedureOBJToConditionCode component.displayCondition/>)
					</#if>
					Minecraft.getInstance().font.draw(event.getPoseStack(),
						<#if hasProcedure(component.text)><@procedureOBJToStringCode component.text/><#else>Component.translatable("gui.${modid}.${registryname}.${component.getName()}")</#if>,
						posX + ${x}, posY + ${y}, ${component.color.getRGB()});
			</#list>

			<#list data.getComponentsOfType("EntityModel") as component>
			<#assign x = component.x / 2>
			<#assign y = (component.y + 17) / 4>
			<#assign y1 = (component.y - 21) / 4>
			{
				Entity modelEntity = <@procedureOBJToConditionCode component.entityModel/>;
				if (modelEntity instanceof LivingEntity entityLiving && modelEntity != null)
				<#if hasProcedure(component.displayCondition)>if (<@procedureOBJToConditionCode component.displayCondition/>) </#if>
					renderBgEntity(posX / 2 + ${x?int}, posY / 2 + ${(y)?int},
					 ${component.scale}, (float) (posY / 2 + (${y1})), entityLiving);
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

	<#if ((data.getComponentsOfType("EntityModel")?size) > 0)>
	protected static void renderBgEntity(int param1, int param2, double param3, float param5, LivingEntity renderTarget) {
		float f1 = (float) Math.atan((double) (param5 / 40.0F));
		PoseStack poseStack = RenderSystem.getModelViewStack();
		poseStack.pushPose();
		poseStack.translate((double) param1, (double) param2, 1050.0D);
		poseStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack secondPoseStack = new PoseStack();
		secondPoseStack.translate(0.0D, 0.0D, 1000.0D);
		secondPoseStack.scale((float) param3, (float) param3, (float) param3);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion secondQuaternion = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(secondQuaternion);
		secondPoseStack.mulPose(quaternion);
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		secondQuaternion.conj();
		dispatcher.overrideCameraOrientation(secondQuaternion);
		dispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			dispatcher.render(renderTarget, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, secondPoseStack, buffer, 15728880);
		});
		buffer.endBatch();
		dispatcher.setRenderShadow(true);
		poseStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}
	</#if>

}
<#-- @formatter:on -->