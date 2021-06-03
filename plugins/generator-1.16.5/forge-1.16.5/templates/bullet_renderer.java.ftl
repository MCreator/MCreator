package ${package}.entity.renderer;

@OnlyIn(Dist.CLIENT) public class ${name}Renderer {

	public static class ModelRegisterHandler {

		@SubscribeEvent @OnlyIn(Dist.CLIENT) public void registerModels(ModelRegistryEvent event) {
			<#if data.bulletModel != "Default">
			RenderingRegistry.registerEntityRenderingHandler(${name}Item.arrow, renderManager -> new CustomRender(renderManager));
			<#else>
			RenderingRegistry.registerEntityRenderingHandler(${name}Item.arrow, renderManager -> new SpriteRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
			</#if>
		}

	}

	<#if data.bulletModel != "Default">
	@OnlyIn(Dist.CLIENT) public static class CustomRender extends EntityRenderer<${name}Item.ArrowCustomEntity> {
		private static final ResourceLocation texture = new ResourceLocation("${modid}:textures/${data.customBulletModelTexture}");

		public CustomRender(EntityRendererManager renderManager) {
			super(renderManager);
		}

		@Override public void render(${name}Item.ArrowCustomEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
			IVertexBuilder vb = bufferIn.getBuffer(RenderType.getEntityCutout(this.getEntityTexture(entityIn)));
			matrixStackIn.push();
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90));
			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90 + MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
			EntityModel model = new ${data.bulletModel}();
			model.render(matrixStackIn, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.0625f);
			matrixStackIn.pop();

			super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		}

		@Override public ResourceLocation getEntityTexture(${name}Item.ArrowCustomEntity entity) {
			return texture;
		}
	}

	<#if data.getModelCode()?? >
		${data.getModelCode().toString()
		.replace("extends ModelBase", "extends EntityModel<Entity>")
		.replace("GlStateManager.translate", "GlStateManager.translated")
		.replace("GlStateManager.scale", "GlStateManager.scaled")
		.replace("RendererModel ", "ModelRenderer ")
		.replace("RendererModel(", "ModelRenderer(")
		.replaceAll("(.*?)\\.cubeList\\.add\\(new\\sModelBox\\(", "addBoxHelper(")
		.replaceAll(",[\n\r\t\\s]+true\\)\\);", ", true);")
		.replaceAll(",[\n\r\t\\s]+false\\)\\);", ", false);")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+e\\)",
		"setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4)")
		.replaceAll("setRotationAngles\\(float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5,[\n\r\t\\s]+Entity[\n\r\t\\s]+entity\\)",
		"setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4)")

		.replaceAll("((super\\.)?)setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+e\\);",
		"")
		.replaceAll("((super\\.)?)setRotationAngles\\(f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5,[\n\r\t\\s]+entity\\);",
		"")

		.replaceAll("render\\(Entity[\n\r\t\\s]+entity,[\n\r\t\\s]+float[\n\r\t\\s]+f,[\n\r\t\\s]+float[\n\r\t\\s]+f1,[\n\r\t\\s]+float[\n\r\t\\s]+f2,[\n\r\t\\s]+float[\n\r\t\\s]+f3,[\n\r\t\\s]+float[\n\r\t\\s]+f4,[\n\r\t\\s]+float[\n\r\t\\s]+f5\\)",
		"render(MatrixStack ms, IVertexBuilder vb, int i1, int i2, float f1, float f2, float f3, float f4)")
		.replaceAll("super\\.render\\(entity,[\n\r\t\\s]+f,[\n\r\t\\s]+f1,[\n\r\t\\s]+f2,[\n\r\t\\s]+f3,[\n\r\t\\s]+f4,[\n\r\t\\s]+f5\\);", "")
		.replace(".render(f5);", ".render(ms, vb, i1, i2, f1, f2, f3, f4);")
		}

		<#if data.getModelCode().contains(".cubeList.add(new")> <#-- if the model is pre 1.15.2 -->
		@OnlyIn(Dist.CLIENT) public static void addBoxHelper(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
			addBoxHelper(renderer, texU, texV, x, y, z, dx, dy, dz, delta, renderer.mirror);
		}

		@OnlyIn(Dist.CLIENT) public static void addBoxHelper(ModelRenderer renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			renderer.mirror = mirror;
			renderer.addBox("", x, y, z, dx, dy, dz, delta, texU, texV);
		}
		</#if>

	</#if>
	</#if>

}
