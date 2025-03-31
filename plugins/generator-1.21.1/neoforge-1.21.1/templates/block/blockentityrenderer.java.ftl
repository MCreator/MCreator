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

		// TODO: texture handling
		texture = ResourceLocation.fromNamespaceAndPath("${modid}", "textures/block/${data.texture}.png");
	}

	@Override public void render(BlockEntity blockEntity, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight) {
		// TODO: rotation if block has rotations enabled

		VertexConsumer builder = renderer.getBuffer(RenderType.entityCutout(texture));
		model.renderToBuffer(matrix, builder, light, overlayLight);
	}

	@SubscribeEvent public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(${JavaModName}BlockEntities.${REGISTRYNAME}.get(), ${name}Renderer::new);
	}

	private static final class CustomHierarchicalModel extends ${data.customModelName.split(":")[0]} {

		private final ModelPart root;

		private final HierarchicalModel animator = new HierarchicalModel<Entity>() {
			@Override public ModelPart root() {
				return root;
			}

			@Override public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
			}
		};

		public CustomHierarchicalModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}

		public ModelPart getRoot() {
			return root;
		}

	}

}
<#-- @formatter:on -->