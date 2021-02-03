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
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">

package ${package};

import net.minecraft.block.material.Material;

@Mod.EventBusSubscriber(modid = "${modid}", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ${name}DimensionSky {
	public static Field field_239208_a_ = ObfuscationReflectionHelper.findField(DimensionRenderInfo.class, "field_239208_a_");
	public static Field skyVBO = ObfuscationReflectionHelper.findField(WorldRenderer.class, "skyVBO");
	public static Field skyVertexFormat = ObfuscationReflectionHelper.findField(WorldRenderer.class, "skyVertexFormat");
	public static Field starVBO = ObfuscationReflectionHelper.findField(WorldRenderer.class, "starVBO");
	public static Field sky2VBO = ObfuscationReflectionHelper.findField(WorldRenderer.class, "sky2VBO");

	private static final ResourceLocation DIM_RENDER_INFO = new ResourceLocation("${modid}", "${registryname}");
	private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("${modid}", "textures/${data.sunTexture}");
	private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("${modid}", "textures/${data.moonTexture}");
	private static final ResourceLocation SKY_TEXTURE = new ResourceLocation("${modid}", "textures/${data.skyTexture}");
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void clientSetup(FMLClientSetupEvent event) {
		try {
			((Map<ResourceLocation, DimensionRenderInfo>) field_239208_a_.get(null)).put(DIM_RENDER_INFO,
				new DimensionRenderInfo(<#if data.imitateOverworldBehaviour>128<#else>Float.NaN</#if>, true,
				<#if data.imitateOverworldBehaviour>DimensionRenderInfo.FogType.NORMAL<#else>DimensionRenderInfo.FogType.NONE</#if>, true, false) {

					@Override public Vector3d func_230494_a_(Vector3d color, float sunHeight) {
						<#if data.airColor?has_content>
							return new Vector3d(${data.airColor.getRed()/255},${data.airColor.getGreen()/255},${data.airColor.getBlue()/255});
						<#else>
							<#if data.imitateOverworldBehaviour>
								return color.mul(sunHeight * 0.94 + 0.06, sunHeight * 0.94 + 0.06, sunHeight * 0.91 + 0.09);
							<#else>
								return color;
							</#if>
						</#if>
					}

					@Override public boolean func_230493_a_(int posX, int posY) {
						return ${data.hasFog};
					}

					@Override
					public ISkyRenderHandler getSkyRenderHandler() {
						return new ISkyRenderHandler() {
							@SuppressWarnings({"deprecation"})
							@Override
							public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
								RenderSystem.disableTexture();
								Vector3d vector3d = world.getSkyColor(mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
								float f = (float) vector3d.x;
								float f1 = (float) vector3d.y;
								float f2 = (float) vector3d.z;
								FogRenderer.applyFog();
								BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
								RenderSystem.depthMask(false);
								RenderSystem.enableFog();
								RenderSystem.color3f(f, f1, f2);
								try {
									((VertexBuffer) skyVBO.get(Minecraft.getInstance().worldRenderer)).bindBuffer();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
								try {
									((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).setupBufferState(0L);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
								try {
									((VertexBuffer) skyVBO.get(Minecraft.getInstance().worldRenderer)).draw(matrixStack.getLast().getMatrix(), 7);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
								VertexBuffer.unbindBuffer();
								try {
									((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).clearBufferState();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
								Matrix4f matrix4f1 = matrixStack.getLast().getMatrix();
								RenderSystem.enableAlphaTest();
								RenderSystem.enableTexture();
								RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
									GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
								RenderSystem.color4f(1f, 1f, 1f, 1f);
								mc.getTextureManager().bindTexture(SKY_TEXTURE);
								bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
								bufferbuilder.pos(matrix4f1, -100, 8f, -100).tex(0.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, 100, 8f, -100).tex(1.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, 100, 8f, 100).tex(1.0F, 1.0F).endVertex();
								bufferbuilder.pos(matrix4f1, -100, 8f, 100).tex(0.0F, 1.0F).endVertex();
								bufferbuilder.finishDrawing();
								WorldVertexBufferUploader.draw(bufferbuilder);
								RenderSystem.disableTexture();
								RenderSystem.disableFog();
								RenderSystem.disableAlphaTest();
								RenderSystem.enableBlend();
								RenderSystem.defaultBlendFunc();
								float[] afloat = world.func_239132_a_().func_230492_a_(world.func_242415_f(partialTicks), partialTicks);
								if (afloat != null) {
									RenderSystem.disableTexture();
									RenderSystem.shadeModel(7425);
									matrixStack.push();
									matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
									float f3 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F;
									matrixStack.rotate(Vector3f.ZP.rotationDegrees(f3));
									matrixStack.rotate(Vector3f.ZP.rotationDegrees(90.0F));
									float f4 = afloat[0];
									float f5 = afloat[1];
									float f6 = afloat[2];
									Matrix4f matrix4f = matrixStack.getLast().getMatrix();
									bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
									bufferbuilder.pos(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, afloat[3]).endVertex();
									for (int j = 0; j <= 16; ++j) {
										float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
										float f8 = MathHelper.sin(f7);
										float f9 = MathHelper.cos(f7);
										bufferbuilder.pos(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3])
											.color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
									}
									bufferbuilder.finishDrawing();
									WorldVertexBufferUploader.draw(bufferbuilder);
									matrixStack.pop();
									RenderSystem.shadeModel(7424);
								}
								RenderSystem.enableTexture();
								RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
									GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
								matrixStack.push();
								float f11 = 1.0F - world.getRainStrength(partialTicks);
								RenderSystem.color4f(1.0F, 1.0F, 1.0F, f11);
								matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
								matrixStack.rotate(Vector3f.XP.rotationDegrees(world.func_242415_f(partialTicks) * 360.0F));
								matrix4f1 = matrixStack.getLast().getMatrix();
								float f12 = 30.0F;
								mc.getTextureManager().bindTexture(SUN_TEXTURES);
								bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
								bufferbuilder.pos(matrix4f1, -f12, 100.0F, -f12).tex(0.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, f12, 100.0F, -f12).tex(1.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, f12, 100.0F, f12).tex(1.0F, 1.0F).endVertex();
								bufferbuilder.pos(matrix4f1, -f12, 100.0F, f12).tex(0.0F, 1.0F).endVertex();
								bufferbuilder.finishDrawing();
								WorldVertexBufferUploader.draw(bufferbuilder);
								f12 = 20.0F;
								mc.getTextureManager().bindTexture(MOON_PHASES_TEXTURES);
								int k = world.getMoonPhase();
								int l = k % 4;
								int i1 = k / 4 % 2;
								float f13 = (float) (l + 0) / 4.0F;
								float f14 = (float) (i1 + 0) / 2.0F;
								float f15 = (float) (l + 1) / 4.0F;
								float f16 = (float) (i1 + 1) / 2.0F;
								bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
								<#if data.phaseTexture>
								bufferbuilder.pos(matrix4f1, -f12, -100.0F, f12).tex(f15, f16).endVertex();
								bufferbuilder.pos(matrix4f1, f12, -100.0F, f12).tex(f13, f16).endVertex();
								bufferbuilder.pos(matrix4f1, f12, -100.0F, -f12).tex(f13, f14).endVertex();
								bufferbuilder.pos(matrix4f1, -f12, -100.0F, -f12).tex(f15, f14).endVertex();
								<#else>
								bufferbuilder.pos(matrix4f1, -f12, -100.0F, f12).tex(0.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, f12, -100.0F, f12).tex(1.0F, 0.0F).endVertex();
								bufferbuilder.pos(matrix4f1, f12, -100.0F, -f12).tex(1.0F, 1.0F).endVertex();
								bufferbuilder.pos(matrix4f1, -f12, -100.0F, -f12).tex(0.0F, 1.0F).endVertex();
								</#if>
								bufferbuilder.finishDrawing();
								WorldVertexBufferUploader.draw(bufferbuilder);
								RenderSystem.disableTexture();
								float f10 = world.getStarBrightness(partialTicks) * f11;
								if (f10 > 0.0F) {
									RenderSystem.color4f(f10, f10, f10, f10);
									try {
										((VertexBuffer) starVBO.get(Minecraft.getInstance().worldRenderer)).bindBuffer();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									try {
										((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).setupBufferState(0L);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									try {
										((VertexBuffer) starVBO.get(Minecraft.getInstance().worldRenderer)).draw(matrixStack.getLast().getMatrix(), 7);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									VertexBuffer.unbindBuffer();
									try {
										((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).clearBufferState();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
								RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
								RenderSystem.disableBlend();
								RenderSystem.enableAlphaTest();
								RenderSystem.enableFog();
								matrixStack.pop();
								RenderSystem.disableTexture();
								RenderSystem.color3f(0.0F, 0.0F, 0.0F);
								double d0 = mc.player.getEyePosition(partialTicks).y - world.getWorldInfo().getVoidFogHeight();
								if (d0 < 0.0D) {
									matrixStack.push();
									matrixStack.translate(0.0D, 12.0D, 0.0D);
									try {
										((VertexBuffer) sky2VBO.get(Minecraft.getInstance().worldRenderer)).bindBuffer();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									try {
										((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).setupBufferState(0L);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									try {
										((VertexBuffer) sky2VBO.get(Minecraft.getInstance().worldRenderer)).draw(matrixStack.getLast().getMatrix(), 7);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									VertexBuffer.unbindBuffer();
									try {
										((VertexFormat) skyVertexFormat.get(Minecraft.getInstance().worldRenderer)).clearBufferState();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
									matrixStack.pop();
								}
								if (world.func_239132_a_().func_239216_b_()) {
									RenderSystem.color3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
								} else {
									RenderSystem.color3f(f, f1, f2);
								}
								RenderSystem.enableTexture();
								RenderSystem.depthMask(true);
								RenderSystem.disableFog();
							}
						};
					}
				});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}