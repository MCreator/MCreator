<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2025, Pylo, opensource contributors
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
<#compress>
<#assign renderModel = data.customModelName.split(":")[0]>
@OnlyIn(Dist.CLIENT)
public class ${name}ItemRenderer${(itemindex??)?then(itemindex, "")} implements NoDataSpecialModelRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("${modid}:textures/item/${data.texture}.png");
    private ${renderModel} model;
    private final EntityModelSet entityModelSet;

    public ${name}ItemRenderer${(itemindex??)?then(itemindex, "")}(${renderModel} model) {
    	this.model = model;
    }

    @Override public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean glint) {
    	poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferSource, model.renderType(TEXTURE), false, glint);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT) public static record Unbaked() implements SpecialModelRenderer.Unbaked {
    	public static final MapCodec<${name}ItemRenderer${(itemindex??)?then(itemindex, "")}.Unbaked> MAP_CODEC = MapCodec.unit(new ${name}ItemRenderer${(itemindex??)?then(itemindex, "")}.Unbaked());

    	@Override public MapCodec<${name}ItemRenderer${(itemindex??)?then(itemindex, "")}.Unbaked> type() {
    		return MAP_CODEC;
    	}

    	@Override public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
    		return new ${name}ItemRenderer${(itemindex??)?then(itemindex, "")}(new ${renderModel}(entityModelSet.bakeLayer(${renderModel}.LAYER_LOCATION)));
    		}
    }
}
</#compress>
<#-- @formatter:on -->