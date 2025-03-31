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
package ${package}.client.renderer.item;

<#compress>
@OnlyIn(Dist.CLIENT)
public class ${name}ItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final EntityModelSet entityModelSet;

    public ${name}ItemRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
        this.entityModelSet = pEntityModelSet;
    }

    @Override public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Model model = <#if data.hasCustomJAVAModel()>new ${data.customModelName.split(":")[0]}(this.entityModelSet.bakeLayer(${data.customModelName.split(":")[0]}.LAYER_LOCATION))<#else>null</#if>;
        ResourceLocation texture = ResourceLocation.parse("${data.texture.format("%s:textures/item/%s")}.png");
        LivingEntity entity = stack.getEntityRepresentation() instanceof LivingEntity le ? le : Minecraft.getInstance().player;
        ClientLevel level = entity.level() instanceof ClientLevel cl ? cl : null;
        <#list data.getModels() as model>
        <#if model.hasCustomJAVAModel()>
        if (<#list model.stateMap.entrySet() as entry>
                ItemProperties.getProperty(stack, ResourceLocation.parse("${generator.map(entry.getKey().getPrefixedName(registryname + "_"), "itemproperties")}"))
                    .call(stack, level, entity, 0) >= ${entry.getValue()?is_boolean?then(entry.getValue()?then("1", "0"), entry.getValue())}F
            <#sep> && </#list>) {
            model = new ${model.customModelName.split(":")[0]}(this.entityModelSet.bakeLayer(${model.customModelName.split(":")[0]}.LAYER_LOCATION));
            texture = ResourceLocation.parse("${model.texture.format("%s:textures/item/%s")}.png");
        }
        </#if>
        </#list>
        if (model == null)
            return;
    	poseStack.pushPose();
        applyTransformation(displayContext, poseStack);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, model.renderType(texture), false, stack.hasFoil());
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void applyTransformation(ItemDisplayContext displayContext, PoseStack poseStack) {
    	switch(displayContext) {
    		case FIXED:
    			poseStack.translate(0.5F, 1.35F, 0.5F);
    			poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
    			poseStack.scale(1.0F, -1.0F, -1.0F);
    			break;
    		case GROUND:
    			poseStack.translate(0.5F, 0.65F, 0.5F);
    			poseStack.scale(0.25F, -0.25F, -0.25F);
    			break;
    		case GUI:
    			poseStack.translate(0.5F, 1.5F, 1F);
    			poseStack.scale(1.0F, -1.0F, -1.0F);
    			break;
    		case HEAD:
    			poseStack.translate(0.5F, 2.45F, 0.5F);
    			poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
    			poseStack.scale(1.0F, -1.0F, -1.0F);
    			break;
    		default:
    			poseStack.translate(0.5F, 2.0F, 0.9F);
    			poseStack.scale(1.0F, -1.0F, -1.0F);
    	}
    }
}
</#compress>
<#-- @formatter:on -->