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
	<#assign rootPart = "context.bakeLayer(ModelLayers.CHICKEN)">
	<#assign model = "ChickenModel">
<#elseif data.mobModelName == "Cod">
	<#assign rootPart = "context.bakeLayer(ModelLayers.COD)">
	<#assign model = "CodModel">
<#elseif data.mobModelName == "Cow">
	<#assign rootPart = "context.bakeLayer(ModelLayers.COW)">
	<#assign model = "CowModel">
<#elseif data.mobModelName == "Creeper">
	<#assign rootPart = "context.bakeLayer(ModelLayers.CREEPER)">
	<#assign model = "CreeperModel">
<#elseif data.mobModelName == "Ghast">
	<#assign rootPart = "context.bakeLayer(ModelLayers.GHAST)">
	<#assign model = "GhastModel">
<#elseif data.mobModelName == "Ocelot">
	<#assign rootPart = "context.bakeLayer(ModelLayers.OCELOT)">
	<#assign model = "OcelotModel">
<#elseif data.mobModelName == "Pig">
	<#assign rootPart = "context.bakeLayer(ModelLayers.PIG)">
	<#assign model = "PigModel">
<#elseif data.mobModelName == "Piglin">
	<#assign rootPart = "context.bakeLayer(ModelLayers.PIGLIN)">
	<#assign model = "PiglinModel">
<#elseif data.mobModelName == "Slime">
	<#assign rootPart = "context.bakeLayer(ModelLayers.SLIME)">
	<#assign model = "SlimeModel">
<#elseif data.mobModelName == "Salmon">
	<#assign rootPart = "context.bakeLayer(ModelLayers.SALMON)">
	<#assign model = "SalmonModel">
<#elseif data.mobModelName == "Spider">
	<#assign rootPart = "context.bakeLayer(ModelLayers.SPIDER)">
	<#assign model = "SpiderModel">
<#elseif data.mobModelName == "Villager">
	<#assign rootPart = "context.bakeLayer(ModelLayers.VILLAGER)">
	<#assign model = "VillagerModel">
<#elseif data.mobModelName == "Silverfish">
	<#assign rootPart = "context.bakeLayer(ModelLayers.SILVERFISH)">
	<#assign model = "SilverfishModel">
<#elseif data.mobModelName == "Witch">
	<#assign rootPart = "context.bakeLayer(ModelLayers.WITCH)">
	<#assign model = "WitchModel">
<#elseif !data.isBuiltInModel()>
	<#assign rootPart = "context.bakeLayer(${data.mobModelName}.LAYER_LOCATION)">
	<#assign model = data.mobModelName>
<#else>
	<#assign rootPart = "context.bakeLayer(ModelLayers.PLAYER)">
	<#assign model = "HumanoidModel">
	<#assign humanoid = true>
</#if>

<#compress>
public class ${name}Renderer extends <#if humanoid>Humanoid</#if>MobRenderer<${name}Entity, LivingEntityRenderState, ${model}> {

	private ${name}Entity entity = null;

	public ${name}Renderer(EntityRendererProvider.Context context) {
		<#if data.animations?has_content>
		super(context, new ${model}(${rootPart}) {
			@Override public void setupAnim(LivingEntityRenderState state) {
				<#if humanoid>
					super.setupAnim(state);
					<@setupAnim/>
				<#else>
					<@setupAnim/>
					super.setupAnim(state);
				</#if>
			}
		}, ${data.modelShadowSize}f);
		<#else>
		super(context, new ${model}(${rootPart}), ${data.modelShadowSize}f);
		</#if>

		<#if humanoid>
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
		</#if>

		<#list data.modelLayers as layer>
		this.addLayer(new RenderLayer<LivingEntityRenderState, ${model}>(this) {
			final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("${modid}:textures/entities/${layer.texture}");

			<#compress>
			@Override public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light,
					LivingEntityRenderState state, float headYaw, float headPitch) {
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
					model.setupAnim(state);
					model.renderToBuffer(poseStack, vertexConsumer, light,
						<#if layer.disableHurtOverlay>OverlayTexture.NO_OVERLAY<#else>LivingEntityRenderer.getOverlayCoords(entity, 0)</#if>);
				<#else>
					this.getParentModel().renderToBuffer(poseStack, vertexConsumer, light,
						<#if layer.disableHurtOverlay>OverlayTexture.NO_OVERLAY<#else>LivingEntityRenderer.getOverlayCoords(entity, 0)</#if>);
				</#if>

				<#if hasProcedure(layer.condition)>}</#if>
			}
			</#compress>
		});
		</#list>
	}

	@Override public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override public void extractRenderState(${name}Entity entity, LivingEntityRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		this.entity = entity;
	}

	@Override public ResourceLocation getTextureLocation(LivingEntityRenderState state) {
		return ResourceLocation.parse("${modid}:textures/entities/${data.mobModelTexture}");
	}

	<#if data.mobModelName == "Villager" || (data.visualScale?? && (data.visualScale.getFixedValue() != 1 || hasProcedure(data.visualScale)))>
	@Override protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
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

	<#if data.transparentModelCondition?? && (hasProcedure(data.transparentModelCondition) || data.transparentModelCondition.getFixedValue())>
	@Override protected boolean isBodyVisible(LivingEntityRenderState state) {
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
	@Override protected boolean isShaking(LivingEntityRenderState state) {
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
</#compress>

<#macro setupAnim>
	<#if !humanoid> <#-- HumanoidModel resets its pose in its setupAnim which is called before this one for this special case -->
	this.root().getAllParts().forEach(ModelPart::resetPose);
	</#if>
	<#list data.animations as animation>
		<#if !animation.walking>
			this.animate(entity.animationState${animation?index}, ${animation.animation}, state.ageInTicks, ${animation.speed}f);
		<#else>
			<#if hasProcedure(animation.condition)>
			if (<@procedureCode animation.condition, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"entity": "entity",
				"world": "entity.level()"
			}, false/>)
			</#if>
			this.animateWalk(${animation.animation}, state.walkAnimationPos, state.walkAnimationSpeed, ${animation.speed}f, ${animation.amplitude}f);
		</#if>
	</#list>
</#macro>