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
	private static GpuBuffer sunBuffer;
	private static GpuBuffer moonBuffer;
	private static GpuBuffer skyboxBuffer;

	<#list dimensions as dimension>
		<#if dimension.enableSkybox || dimension.enableSunMoon>
			private static final ResourceKey ${dimension.getModElement().getRegistryNameUpper()}
				= ResourceKey.create(Registries.DIMENSION, Identifier.parse("${modid}:${dimension.getModElement().getRegistryName()}"));
		</#if>
		<#if dimension.enableSkybox>
			private static final Identifier ${dimension.getModElement().getRegistryNameUpper()}_SKYBOX
				= Identifier.parse("${modid}:textures/skybox/${dimension.getModElement().getRegistryName()}.png");
		</#if>
		<#if dimension.enableSunMoon>
			private static final Identifier ${dimension.getModElement().getRegistryNameUpper()}_SUN
				= Identifier.parse("${modid}:textures/${dimension.sunTexture}.png");
			private static final Identifier ${dimension.getModElement().getRegistryNameUpper()}_MOON
				= Identifier.parse("${modid}:textures/${dimension.moonTexture}.png");
		</#if>
	</#list>

	private static void initBuffers() {
		if (sunBuffer == null) sunBuffer = buildSunBuffer();
		if (moonBuffer == null) moonBuffer = buildMoonBuffer();
		if (skyboxBuffer == null) skyboxBuffer = buildSkyboxBuffer();
	}

	@SubscribeEvent
	public static void renderSky(RenderLevelStageEvent.AfterSky event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
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

	public static void renderCustomSun(RenderLevelStageEvent.AfterSky event, Identifier textureId) {
		initBuffers();
		Minecraft mc = Minecraft.getInstance();
		PoseStack poseStack = event.getPoseStack();
		SkyRenderState state = event.getLevelRenderState().skyRenderState;

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.XP.rotation(state.sunAngle));

		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.mul(poseStack.last().pose());
		modelViewStack.translate(0.0F, 100.0F, 0.0F);
		modelViewStack.scale(30.0F, 1.0F, 30.0F);

		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
			.writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, state.rainBrightness), new Vector3f(), new Matrix4f());

		GpuTextureView color = mc.getMainRenderTarget().getColorTextureView();
		GpuTextureView depth = mc.getMainRenderTarget().getDepthTextureView();
		GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).getBuffer(6);
		AbstractTexture texture = mc.getTextureManager().getTexture(textureId);

		try (RenderPass renderPass = RenderSystem.getDevice()
				.createCommandEncoder()
				.createRenderPass(() -> "Custom Sun", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
			renderPass.setPipeline(RenderPipelines.CELESTIAL);
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);
			renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
			renderPass.setVertexBuffer(0, sunBuffer);
			renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).type());
			renderPass.drawIndexed(0, 0, 6, 1);
		}

		modelViewStack.popMatrix();
		poseStack.popPose();
	}

	public static void renderCustomMoon(RenderLevelStageEvent.AfterSky event, Identifier textureId) {
		initBuffers();
		Minecraft mc = Minecraft.getInstance();
		PoseStack poseStack = event.getPoseStack();
		SkyRenderState state = event.getLevelRenderState().skyRenderState;

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
		poseStack.mulPose(Axis.XP.rotation(state.moonAngle));

		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.mul(poseStack.last().pose());
		modelViewStack.translate(0.0F, 100.0F, 0.0F);
		modelViewStack.scale(20.0F, 1.0F, 20.0F);

		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
			.writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, state.rainBrightness), new Vector3f(), new Matrix4f());

		GpuTextureView color = mc.getMainRenderTarget().getColorTextureView();
		GpuTextureView depth = mc.getMainRenderTarget().getDepthTextureView();
		GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).getBuffer(6);
		AbstractTexture texture = mc.getTextureManager().getTexture(textureId);

		int baseVertex = state.moonPhase.index() * 4;

		try (RenderPass renderPass = RenderSystem.getDevice()
				.createCommandEncoder()
				.createRenderPass(() -> "Custom Moon", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
			renderPass.setPipeline(RenderPipelines.CELESTIAL);
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);
			renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
			renderPass.setVertexBuffer(0, moonBuffer);
			renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).type());
			renderPass.drawIndexed(baseVertex, 0, 6, 1);
		}

		modelViewStack.popMatrix();
		poseStack.popPose();
	}

	public static void renderCustomSkybox(RenderLevelStageEvent.AfterSky event, Identifier textureId) {
		initBuffers();
		Minecraft mc = Minecraft.getInstance();
		PoseStack poseStack = event.getPoseStack();

		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.mul(poseStack.last().pose());

		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
			.writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());

		GpuTextureView color = mc.getMainRenderTarget().getColorTextureView();
		GpuTextureView depth = mc.getMainRenderTarget().getDepthTextureView();
		GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).getBuffer(36);
		AbstractTexture texture = mc.getTextureManager().getTexture(textureId);

		try (RenderPass renderPass = RenderSystem.getDevice()
				.createCommandEncoder()
				.createRenderPass(() -> "Custom Skybox", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
			renderPass.setPipeline(RenderPipelines.END_SKY);
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);
			renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
			renderPass.setVertexBuffer(0, skyboxBuffer);
			renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).type());
			renderPass.drawIndexed(0, 0, 36, 1);
		}

		modelViewStack.popMatrix();
	}

	private static GpuBuffer buildSunBuffer() {
		VertexFormat format = DefaultVertexFormat.POSITION_TEX;
		try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(4 * format.getVertexSize())) {
			BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, format);
			bufferBuilder.addVertex(-1.0F, 0.0F, -1.0F).setUv(0.0F, 0.0F);
			bufferBuilder.addVertex(1.0F, 0.0F, -1.0F).setUv(1.0F, 0.0F);
			bufferBuilder.addVertex(1.0F, 0.0F, 1.0F).setUv(1.0F, 1.0F);
			bufferBuilder.addVertex(-1.0F, 0.0F, 1.0F).setUv(0.0F, 1.0F);
			try (MeshData mesh = bufferBuilder.buildOrThrow()) {
				return RenderSystem.getDevice().createBuffer(() -> "Custom Sun", 32, mesh.vertexBuffer());
			}
		}
	}

	private static GpuBuffer buildMoonBuffer() {
		VertexFormat format = DefaultVertexFormat.POSITION_TEX;
		try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(8 * 4 * format.getVertexSize())) {
			BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, format);
			for (int k = 0; k < 8; k++) {
				int l = k % 4;
				int i1 = k / 4 % 2;
				float f13 = (float)(l + 0) / 4.0F;
				float f14 = (float)(i1 + 0) / 2.0F;
				float f15 = (float)(l + 1) / 4.0F;
				float f16 = (float)(i1 + 1) / 2.0F;

				bufferBuilder.addVertex(-1.0F, 0.0F, -1.0F).setUv(f15, f16);
				bufferBuilder.addVertex(1.0F, 0.0F, -1.0F).setUv(f13, f16);
				bufferBuilder.addVertex(1.0F, 0.0F, 1.0F).setUv(f13, f14);
				bufferBuilder.addVertex(-1.0F, 0.0F, 1.0F).setUv(f15, f14);
			}
			try (MeshData mesh = bufferBuilder.buildOrThrow()) {
				return RenderSystem.getDevice().createBuffer(() -> "Custom Moon", 32, mesh.vertexBuffer());
			}
		}
	}

	private static GpuBuffer buildSkyboxBuffer() {
		VertexFormat format = DefaultVertexFormat.POSITION_TEX_COLOR;
		try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(24 * format.getVertexSize())) {
			BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, format);
			float distance = 100.0F;
			float size = 100.0F;
			int color = 0xFFFFFFFF;

			addSkyboxFace(bufferBuilder, -size, distance, -size, 1.0F / 4.0F, 1.0F / 3.0F, size, distance, -size, 2.0F / 4.0F, 1.0F / 3.0F, size, distance, size, 2.0F / 4.0F, 0.0F, -size, distance, size, 1.0F / 4.0F, 0.0F, color);
			addSkyboxFace(bufferBuilder, -size, -distance, -size, 1.0F / 4.0F, 2.0F / 3.0F, -size, -distance, size, 1.0F / 4.0F, 3.0F / 3.0F, size, -distance, size, 2.0F / 4.0F, 3.0F / 3.0F, size, -distance, -size, 2.0F / 4.0F, 2.0F / 3.0F, color);
			addSkyboxFace(bufferBuilder, -distance, -size, size, 0.0F, 2.0F / 3.0F, -distance, -size, -size, 1.0F / 4.0F, 2.0F / 3.0F, -distance, size, -size, 1.0F / 4.0F, 1.0F / 3.0F, -distance, size, size, 0.0F, 1.0F / 3.0F, color);
			addSkyboxFace(bufferBuilder, -size, -size, -distance, 1.0F / 4.0F, 2.0F / 3.0F, size, -size, -distance, 2.0F / 4.0F, 2.0F / 3.0F, size, size, -distance, 2.0F / 4.0F, 1.0F / 3.0F, -size, size, -distance, 1.0F / 4.0F, 1.0F / 3.0F, color);
			addSkyboxFace(bufferBuilder, distance, -size, -size, 2.0F / 4.0F, 2.0F / 3.0F, distance, -size, size, 3.0F / 4.0F, 2.0F / 3.0F, distance, size, size, 3.0F / 4.0F, 1.0F / 3.0F, distance, size, -size, 2.0F / 4.0F, 1.0F / 3.0F, color);
			addSkyboxFace(bufferBuilder, size, -size, distance, 3.0F / 4.0F, 2.0F / 3.0F, -size, -size, distance, 4.0F / 4.0F, 2.0F / 3.0F, -size, size, distance, 4.0F / 4.0F, 1.0F / 3.0F, size, size, distance, 3.0F / 4.0F, 1.0F / 3.0F, color);

			try (MeshData meshData = bufferBuilder.buildOrThrow()) {
				return RenderSystem.getDevice().createBuffer(() -> "Custom Skybox", 40, meshData.vertexBuffer());
			}
		}
	}

	private static void addSkyboxFace(BufferBuilder bufferBuilder, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, float x4, float y4, float z4, float u4, float v4, int color) {
		bufferBuilder.addVertex(x1, y1, z1).setUv(u1, v1).setColor(color);
		bufferBuilder.addVertex(x2, y2, z2).setUv(u2, v2).setColor(color);
		bufferBuilder.addVertex(x3, y3, z3).setUv(u3, v3).setColor(color);
		bufferBuilder.addVertex(x4, y4, z4).setUv(u4, v4).setColor(color);
	}
}

<#-- @formatter:on -->