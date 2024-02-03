<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2022, Pylo, opensource contributors
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

<#include "../procedures.java.ftl">

package ${package}.client.renderer;

<#assign humanoid = false>
<#assign model = "HumanoidModel">

<#if data.mobModelName == "Chicken">
	<#assign super = "super(context, new ChickenModel(context.bakeLayer(ModelLayers.CHICKEN)), " + data.modelShadowSize + "f);">
	<#assign model = "ChickenModel">
<#elseif data.mobModelName == "Cod">
	<#assign super = "super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), " + data.modelShadowSize + "f);">
	<#assign model = "CodModel">
<#elseif data.mobModelName == "Cow">
	<#assign super = "super(context, new CowModel(context.bakeLayer(ModelLayers.COW)), " + data.modelShadowSize + "f);">
	<#assign model = "CowModel">
<#elseif data.mobModelName == "Creeper">
	<#assign super = "super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), " + data.modelShadowSize + "f);">
	<#assign model = "CreeperModel">
<#elseif data.mobModelName == "Ghast">
	<#assign super = "super(context, new GhastModel(context.bakeLayer(ModelLayers.GHAST)), " + data.modelShadowSize + "f);">
	<#assign model = "GhastModel">
<#elseif data.mobModelName == "Ocelot">
	<#assign super = "super(context, new OcelotModel(context.bakeLayer(ModelLayers.OCELOT)), " + data.modelShadowSize + "f);">
	<#assign model = "OcelotModel">
<#elseif data.mobModelName == "Pig">
	<#assign super = "super(context, new PigModel(context.bakeLayer(ModelLayers.PIG)), " + data.modelShadowSize + "f);">
	<#assign model = "PigModel">
<#elseif data.mobModelName == "Piglin">
	<#assign super = "super(context, new PiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), " + data.modelShadowSize + "f);">
	<#assign model = "PiglinModel">
<#elseif data.mobModelName == "Slime">
	<#assign super = "super(context, new SlimeModel(context.bakeLayer(ModelLayers.SLIME)), " + data.modelShadowSize + "f);">
	<#assign model = "SlimeModel">
<#elseif data.mobModelName == "Salmon">
	<#assign super = "super(context, new SalmonModel(context.bakeLayer(ModelLayers.SALMON)), " + data.modelShadowSize + "f);">
	<#assign model = "SalmonModel">
<#elseif data.mobModelName == "Spider">
	<#assign super = "super(context, new SpiderModel(context.bakeLayer(ModelLayers.SPIDER)), " + data.modelShadowSize + "f);">
	<#assign model = "SpiderModel">
<#elseif data.mobModelName == "Villager">
	<#assign super = "super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), " + data.modelShadowSize + "f);">
	<#assign model = "VillagerModel">
<#elseif data.mobModelName == "Silverfish">
	<#assign super = "super(context, new SilverfishModel(context.bakeLayer(ModelLayers.SILVERFISH)), " + data.modelShadowSize + "f);">
	<#assign model = "SilverfishModel">
<#elseif data.mobModelName == "Witch">
	<#assign super = "super(context, new WitchModel(context.bakeLayer(ModelLayers.WITCH)), " + data.modelShadowSize + "f);">
	<#assign model = "WitchModel">
<#elseif !data.isBuiltInModel()>
	<#assign super = "super(context, new ${data.mobModelName}(context.bakeLayer(${data.mobModelName}.LAYER_LOCATION)), " + data.modelShadowSize + "f);">
	<#assign model = data.mobModelName>
<#else>
	<#assign super = "super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), " + data.modelShadowSize + "f);">
	<#assign model = "HumanoidModel">
	<#assign humanoid = true>
</#if>

<#assign model = model + "<" + name + "Entity>">

public class ${name}Renderer extends <#if humanoid>Humanoid</#if>MobRenderer<${name}Entity, ${model}> {

	public ${name}Renderer(EntityRendererProvider.Context context) {
		${super}

		<#if humanoid>
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
		</#if>

		<#list data.modelLayers as layer>
		this.addLayer(new RenderLayer<${name}Entity, ${model}>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("${modid}:textures/entities/${layer.texture}");

			<#compress>
			@Override public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light,
						${name}Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				<#if hasProcedure(layer.condition)>
				Level world = entity.level();
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				if (<@procedureOBJToConditionCode layer.condition/>) {
				</#if>

				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.<#if layer.glow>eyes<#else>entityCutoutNoCull</#if>(LAYER_TEXTURE));
				<#if layer.model != "Default">
					EntityModel model = new ${layer.model}(Minecraft.getInstance().getEntityModels().bakeLayer(${layer.model}.LAYER_LOCATION));
					this.getParentModel().copyPropertiesTo(model);
					model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
					model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
					model.renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
				<#else>
					this.getParentModel().renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
				</#if>

				<#if hasProcedure(layer.condition)>}</#if>
			}
			</#compress>
		});
		</#list>
	}

	<#if data.mobModelName == "Villager" || (data.visualScale?? && (data.visualScale.getFixedValue() != 1 || hasProcedure(data.visualScale)))>
	@Override protected void scale(${name}Entity entity, PoseStack poseStack, float f) {
		<#if hasProcedure(data.visualScale)>
			Level world = entity.level();
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			float scale = (float) <@procedureOBJToNumberCode data.visualScale/>;
			poseStack.scale(scale, scale, scale);
		<#elseif data.visualScale?? && data.visualScale.getFixedValue() != 1>
			poseStack.scale(${data.visualScale.getFixedValue()}f, ${data.visualScale.getFixedValue()}f, ${data.visualScale.getFixedValue()}f);
		</#if>
		<#if data.mobModelName == "Villager">
			poseStack.scale(0.9375f, 0.9375f, 0.9375f);
		</#if>
	}
	</#if>

	@Override public ResourceLocation getTextureLocation(${name}Entity entity) {
		return new ResourceLocation("${modid}:textures/entities/${data.mobModelTexture}");
	}

	<#if data.transparentModelCondition?? && (hasProcedure(data.transparentModelCondition) || data.transparentModelCondition.getFixedValue())>
	@Override protected boolean isBodyVisible(${name}Entity entity) {
		<#if hasProcedure(data.transparentModelCondition)>
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		</#if>
		return <@procedureOBJToConditionCode data.transparentModelCondition false true/>;
	}
	</#if>

	<#if data.isShakingCondition?? && (hasProcedure(data.isShakingCondition) || data.isShakingCondition.getFixedValue())>
	@Override protected boolean isShaking(${name}Entity entity) {
		<#if hasProcedure(data.isShakingCondition)>
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		</#if>
		return <@procedureOBJToConditionCode data.isShakingCondition/>;
	}
	</#if>

}
