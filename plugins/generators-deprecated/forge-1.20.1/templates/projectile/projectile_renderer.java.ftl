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
package ${package}.client.renderer;

import com.mojang.math.Axis;

public class ${name}Renderer extends EntityRenderer<${name}Entity> {

	private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/entities/${data.customModelTexture}");

	private final ${data.entityModel} model;

	public ${name}Renderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ${data.entityModel}(context.bakeLayer(${data.entityModel}.LAYER_LOCATION));
	}

	@Override public void render(${name}Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();

		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override public ResourceLocation getTextureLocation(${name}Entity entity) {
		return texture;
	}

}
<#-- @formatter:on -->