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
package ${package}.client.renderer;

public class ${name}Renderer extends MobRenderer {

	public ${name}Renderer(EntityRendererProvider.Context context) {
		<#if data.mobModelName == "Chicken">
		super(context, new ChickenModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Cow">
		super(context, new CowModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Creeper">
		super(context, new CreeperModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Ghast">
		super(context, new GhastModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Pig">
		super(context, new PigModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Slime">
		super(context, new SlimeModel(0), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Spider">
		super(context, new SpiderModel(), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Villager">
		super(context, new VillagerModel(0), ${data.modelShadowSize});
		<#elseif data.mobModelName == "Silverfish">
		super(context, new SilverfishModel(), ${data.modelShadowSize});
		<#elseif !data.isBuiltInModel()>
		super(context, new ${data.bulletModel}(context.bakeLayer(${data.bulletModel}.LAYER_LOCATION)), ${data.modelShadowSize});
		<#else>
		super(context, new BipedModel(0), ${data.modelShadowSize});
		this.addLayer(new BipedArmorLayer(customRender, new BipedModel(0.5f), new BipedModel(1)));
		</#if>

		<#if data.mobModelGlowTexture?has_content>
		this.addLayer(new GlowingLayer<>(this) {
			@Override public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing,
					float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(new ResourceLocation("${modid}:textures/${data.mobModelGlowTexture}")));
				this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			}
		});
		</#if>
	}

	@Override public ResourceLocation getEntityTexture(Entity entity) { 
		return new ResourceLocation("${modid}:textures/${data.mobModelTexture}"); 
	}

}

