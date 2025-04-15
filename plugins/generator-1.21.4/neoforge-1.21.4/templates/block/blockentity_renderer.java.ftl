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

	private final LivingEntityRenderState renderState;

	${name}Renderer(BlockEntityRendererProvider.Context context) {
		this.model = new CustomHierarchicalModel(context.bakeLayer(${data.customModelName.split(":")[0]}.LAYER_LOCATION));
		this.texture = ResourceLocation.parse("${data.texture.format("%s:textures/block/%s")}.png");
		this.renderState = new LivingEntityRenderState();
	}

	private void updateRenderState(BlockEntity blockEntity, float partialTick) {
		renderState.ageInTicks = blockEntity.getLevel().getGameTime() + partialTick;
	}

	@Override public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource renderer, int light, int overlayLight) {
		<#compress>
		updateRenderState(blockEntity, partialTick);
		poseStack.pushPose();
		poseStack.scale(-1, -1, 1);
		poseStack.translate(-0.5, -0.5, 0.5);
		<#if data.rotationMode != 0>
			BlockState state = blockEntity.getBlockState();
			<#if data.rotationMode != 5>
			Direction facing = state.getValue(${name}Block.FACING);
            switch (facing) {
				case NORTH -> {}
				case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
				case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
				case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            	<#if data.rotationMode == 2 || data.rotationMode == 4>
            		case UP -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
            		case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(-90));
				</#if>
			}
			<#if data.enablePitch>
			if (facing != Direction.UP && facing != Direction.DOWN) {
				switch (state.getValue(${name}Block.FACE)) {
					case FLOOR -> {}
					case WALL -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
					case CEILING -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
				};
			}
			</#if>
			<#else>
            switch (state.getValue(${name}Block.AXIS)) {
				case X -> poseStack.mulPose(Axis.ZN.rotationDegrees(90));
				case Y -> {}
				case Z -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
			}
			</#if>
		</#if>
		poseStack.translate(0, -1, 0);
		VertexConsumer builder = renderer.getBuffer(RenderType.entityCutout(texture));
		model.setupAnim(renderState);
		model.renderToBuffer(poseStack, builder, light, overlayLight);
		poseStack.popPose();
		</#compress>
	}

	@SubscribeEvent public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(${JavaModName}BlockEntities.${REGISTRYNAME}.get(), ${name}Renderer::new);
	}

	private static final class CustomHierarchicalModel extends ${data.customModelName.split(":")[0]} {

		public CustomHierarchicalModel(ModelPart root) {
			super(root);
		}

		@Override public void setupAnim(LivingEntityRenderState state) {
			this.root().getAllParts().forEach(ModelPart::resetPose);
			super.setupAnim(state);
		}

		public ModelPart getRoot() {
			return root;
		}

	}

}

<#-- @formatter:on -->