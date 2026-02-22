<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2026, Pylo, opensource contributors
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

package ${package}.client;

@EventBusSubscriber(Dist.CLIENT)
public class ${JavaModName}SkyboxRenderer {
	<#list dimensions as dimension>
		<#if dimension.enableSkybox || dimension.enableSunMoon>
			private static final ResourceKey ${dimension.getModElement().getRegistryNameUpper()}
				= ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("${modid}:${dimension.getModElement().getRegistryName()}"));
		</#if>
		<#if dimension.enableSkybox>
			private static final ResourceLocation ${dimension.getModElement().getRegistryNameUpper()}_SKYBOX
				= ResourceLocation.parse("${modid}:textures/skybox/${dimension.getModElement().getRegistryName()}.png");
		</#if>
		<#if dimension.enableSunMoon>
			private static final ResourceLocation ${dimension.getModElement().getRegistryNameUpper()}_SUN
				= ResourceLocation.parse("${modid}:textures/${dimension.sunTexture}.png");
			private static final ResourceLocation ${dimension.getModElement().getRegistryNameUpper()}_MOON
				= ResourceLocation.parse("${modid}:textures/${dimension.moonTexture}.png");
		</#if>
	</#list>

	@SubscribeEvent
	public static void renderSky(RenderLevelStageEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
		<#list dimensions as dimension>
			<#if dimension.enableSkybox || dimension.enableSunMoon>
				if (mc.player.level().dimension() == ${dimension.getModElement().getRegistryNameUpper()}) {
					<#if dimension.enableSkybox>
						renderCustomSkybox(event, ${dimension.getModElement().getRegistryNameUpper()}_SKYBOX);
					</#if>
					<#if dimension.enableSunMoon>
						renderCustomSun(event, ${dimension.getModElement().getRegistryNameUpper()}_SUN);
						renderCustomMoon(event, ${dimension.getModElement().getRegistryNameUpper()}_MOON);
					</#if>
				}
			</#if>
		</#list>
	}

	public static void renderCustomSun(RenderLevelStageEvent event, ResourceLocation texture) {
		Minecraft mc = Minecraft.getInstance();
		PoseStack posestack = event.getPoseStack();
		posestack.pushPose();
		posestack.mulPose(event.getModelViewMatrix());
		GlStateManager._enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager._depthMask(false);
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		float f11 = 1.0F - mc.player.level().getRainLevel(partialTick);
		RenderSystem.setShaderColor(1, 1, 1, f11);
		posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		posestack.mulPose(Axis.XP.rotationDegrees(mc.player.level().getTimeOfDay(partialTick) * 360.0F));
		Matrix4f matrix4f1 = posestack.last().pose();
		float f12 = 30.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(0.0F, 0.0F);
		bufferbuilder.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(1.0F, 0.0F);
		bufferbuilder.addVertex(matrix4f1, f12, 100.0F, f12).setUv(1.0F, 1.0F);
		bufferbuilder.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(0.0F, 1.0F);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GlStateManager._disableBlend();
		RenderSystem.defaultBlendFunc();
		GlStateManager._depthMask(true);
		posestack.popPose();
	}

	public static void renderCustomMoon(RenderLevelStageEvent event, ResourceLocation texture) {
		Minecraft mc = Minecraft.getInstance();
		PoseStack posestack = event.getPoseStack();
		posestack.pushPose();
		posestack.mulPose(event.getModelViewMatrix());
		GlStateManager._enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager._depthMask(false);
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		float f11 = 1.0F - mc.player.level().getRainLevel(partialTick);
		RenderSystem.setShaderColor(1, 1, 1, f11);
		posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		posestack.mulPose(Axis.XP.rotationDegrees(mc.player.level().getTimeOfDay(partialTick) * 360.0F));
		Matrix4f matrix4f1 = posestack.last().pose();
		float f12 = 20.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		int k = mc.player.level().getMoonPhase();
		int l = k % 4;
		int i1 = k / 4 % 2;
		float f13 = (float)(l + 0) / 4.0F;
		float f14 = (float)(i1 + 0) / 2.0F;
		float f15 = (float)(l + 1) / 4.0F;
		float f16 = (float)(i1 + 1) / 2.0F;
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.addVertex(matrix4f1, -f12, -100.0F, f12).setUv(f15, f16);
		bufferbuilder.addVertex(matrix4f1, f12, -100.0F, f12).setUv(f13, f16);
		bufferbuilder.addVertex(matrix4f1, f12, -100.0F, -f12).setUv(f13, f14);
		bufferbuilder.addVertex(matrix4f1, -f12, -100.0F, -f12).setUv(f15, f14);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GlStateManager._disableBlend();
		RenderSystem.defaultBlendFunc();
		GlStateManager._depthMask(true);
		posestack.popPose();
	}

	public static void renderCustomSkybox(RenderLevelStageEvent event, ResourceLocation texture) {
		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();
		poseStack.mulPose(event.getModelViewMatrix());
		GlStateManager._enableBlend();
		RenderSystem.defaultBlendFunc();
		GlStateManager._depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		Tesselator tesselator = Tesselator.getInstance();
		float distance = 100.0F;
		float size = 100.0F;
		renderSkyboxQuad(poseStack, tesselator, -size, distance, -size, 1.0F / 4.0F, 1.0F / 3.0F, size, distance, -size, 2.0F / 4.0F, 1.0F / 3.0F, size, distance, size, 2.0F / 4.0F, 0.0F, -size, distance, size, 1.0F / 4.0F, 0.0F);
		renderSkyboxQuad(poseStack, tesselator, -size, -distance, -size, 1.0F / 4.0F, 2.0F / 3.0F, -size, -distance, size, 1.0F / 4.0F, 3.0F / 3.0F, size, -distance, size, 2.0F / 4.0F, 3.0F / 3.0F, size, -distance, -size, 2.0F / 4.0F, 2.0F / 3.0F);
		renderSkyboxQuad(poseStack, tesselator, -distance, -size, size, 0.0F, 2.0F / 3.0F, -distance, -size, -size, 1.0F / 4.0F, 2.0F / 3.0F, -distance, size, -size, 1.0F / 4.0F, 1.0F / 3.0F, -distance, size, size, 0.0F, 1.0F / 3.0F);
		renderSkyboxQuad(poseStack, tesselator, -size, -size, -distance, 1.0F / 4.0F, 2.0F / 3.0F, size, -size, -distance, 2.0F / 4.0F, 2.0F / 3.0F, size, size, -distance, 2.0F / 4.0F, 1.0F / 3.0F, -size, size, -distance, 1.0F / 4.0F, 1.0F / 3.0F);
		renderSkyboxQuad(poseStack, tesselator, distance, -size, -size, 2.0F / 4.0F, 2.0F / 3.0F, distance, -size, size, 3.0F / 4.0F, 2.0F / 3.0F, distance, size, size, 3.0F / 4.0F, 1.0F / 3.0F, distance, size, -size, 2.0F / 4.0F, 1.0F / 3.0F);
		renderSkyboxQuad(poseStack, tesselator, size, -size, distance, 3.0F / 4.0F, 2.0F / 3.0F, -size, -size, distance, 4.0F / 4.0F, 2.0F / 3.0F, -size, size, distance, 4.0F / 4.0F, 1.0F / 3.0F, size, size, distance, 3.0F / 4.0F, 1.0F / 3.0F);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GlStateManager._depthMask(true);
		GlStateManager._disableBlend();
		poseStack.popPose();
	}

	private static void renderSkyboxQuad(PoseStack poseStack, Tesselator tesselator, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3,
			float u3, float v3, float x4, float y4, float z4, float u4, float v4) {
		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.addVertex(matrix, x1, y1, z1).setUv(u1, v1);
		buffer.addVertex(matrix, x2, y2, z2).setUv(u2, v2);
		buffer.addVertex(matrix, x3, y3, z3).setUv(u3, v3);
		buffer.addVertex(matrix, x4, y4, z4).setUv(u4, v4);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
	}
}

<#-- @formatter:on -->