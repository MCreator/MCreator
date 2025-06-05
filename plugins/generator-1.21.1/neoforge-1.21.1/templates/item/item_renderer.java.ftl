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
	private final ItemStack transformSource;

	public ${name}ItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
		super(blockEntityRenderDispatcher, entityModelSet);
		this.entityModelSet = entityModelSet;
		this.transformSource = new ItemStack(${JavaModName}Items.${REGISTRYNAME}.get());
	}

	@Override public void renderByItem(ItemStack itemstack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Model model = <#if data.hasCustomJAVAModel()>new ${data.customModelName.split(":")[0]}(this.entityModelSet.bakeLayer(${data.customModelName.split(":")[0]}.LAYER_LOCATION))<#else>null</#if>;
		ResourceLocation texture = ResourceLocation.parse("${data.texture.format("%s:textures/item/%s")}.png");
		<#list data.getModels() as model>
			<#if model.hasCustomJAVAModel()>
			if (<#list model.stateMap.entrySet() as entry>
					ItemProperties.getProperty(itemstack, ResourceLocation.parse("${generator.map(entry.getKey().getPrefixedName(registryname + "_"), "itemproperties")}"))
						.call(itemstack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0) >= ${entry.getValue()?is_boolean?then(entry.getValue()?then("1", "0"), entry.getValue())}
				<#sep> && </#list>) {
				model = new ${model.customModelName.split(":")[0]}(this.entityModelSet.bakeLayer(${model.customModelName.split(":")[0]}.LAYER_LOCATION));
				texture = ResourceLocation.parse("${model.texture.format("%s:textures/item/%s")}.png");
			}
			</#if>
		</#list>
		if (model == null) return;

		poseStack.pushPose();
		Minecraft.getInstance().getItemRenderer().getModel(this.transformSource, null, null, 0).applyTransform(displayContext, poseStack, isLeftHand(displayContext));
		poseStack.translate(0.5, isInventory(displayContext) ? 1.5 : 2, 0.5);
		poseStack.scale(1, -1, displayContext == ItemDisplayContext.GUI ? -1 : 1);
		VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, model.renderType(texture), false, itemstack.hasFoil());
		model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
		poseStack.popPose();
	}

	private static boolean isLeftHand(ItemDisplayContext type) {
		return type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

	private static boolean isInventory(ItemDisplayContext type) {
		return type == ItemDisplayContext.GUI || type == ItemDisplayContext.FIXED;
	}

}
</#compress>
<#-- @formatter:on -->