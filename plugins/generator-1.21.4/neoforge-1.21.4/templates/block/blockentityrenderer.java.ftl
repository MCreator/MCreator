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
package ${package}.client.renderer.block;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public class ${name}Renderer implements BlockEntityRenderer<BlockEntity> {

	private final CustomHierarchicalModel model;
	private final ResourceLocation texture;

	${name}Renderer(BlockEntityRendererProvider.Context context) {
		model = new CustomHierarchicalModel(context.bakeLayer(${data.customModelName.split(":")[0]}.LAYER_LOCATION));

		texture = ResourceLocation.parse("${data.texture.format("%s:textures/block/%s")}.png");
	}

	@Override public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource renderer, int light, int overlayLight) {
		poseStack.pushPose();

		poseStack.translate(0.5, 1.0, 0.5);
		poseStack.mulPose(Axis.XP.rotationDegrees(180));

		<#if data.rotationMode != 0>
			DirectionProperty facing = state.getValue(${name}Block.FACING);
        	<#if data.rotationMode != 5>
				<#assign pitch = (data.rotationMode == 1 || data.rotationMode == 3) && data.enablePitch>
        	    switch (facing) {
					case NORTH -> {}
					case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
					case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
					case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
        	    	<#if data.rotationMode == 2 || data.rotationMode == 4>
        	    		case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
        	    		case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
					</#if>
				}
				<#if data.enablePitch>
				if (facing != UP && facing != DOWN) {
					switch (state.getValue(${name}Block.FACE)) {
						case FLOOR -> {}
						case WALL -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
						case CEILING -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
					};
				}
				</#if>
			<#else>
        	    switch (state.getValue(${name}Block.AXIS)) {
					case X -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
					case Y -> {}
					case Z -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
				}
			</#if>
		</#if>

		VertexConsumer builder = renderer.getBuffer(RenderType.entityCutout(texture));
		model.renderToBuffer(poseStack, builder, light, overlayLight);
		poseStack.popPose();
	}

	@SubscribeEvent public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(${JavaModName}BlockEntities.${REGISTRYNAME}.get(), ${name}Renderer::new);
	}

	private static final class CustomHierarchicalModel extends ${data.customModelName.split(":")[0]} {

		public CustomHierarchicalModel(ModelPart root) {
			super(root);
		}

		@Override public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			this.root().getAllParts().forEach(ModelPart::resetPose);
		}

		public ModelPart getRoot() {
			return root;
		}

	}

}

<#-- @formatter:on -->