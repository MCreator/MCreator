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

<#assign models = []>
<#if data.hasJavaModel()>
    <#assign models += [[-1, data.customModelName.split(":")[0], data.texture]]>
</#if>
<#list data.getModels() as model>
    <#if model.hasJavaModel()>
        <#assign models += [[model?index, model.customModelName.split(":")[0], model.texture]]>
    </#if>
</#list>

<#compress>
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public class ${name}ItemRenderer implements NoDataSpecialModelRenderer {
    private static final Map<Integer, Function<EntityModelSet, ${name}ItemRenderer>> MODELS = Map.ofEntries(
        <#list models as model>
            Map.entry(${model[0]}, modelSet -> new ${name}ItemRenderer(new ${model[1]}(modelSet.bakeLayer(${model[1]}.LAYER_LOCATION)),
                ResourceLocation.parse("${modid}:textures/item/${model[2]}.png")))<#sep>,
        </#list>
    );

    private final Model model;
    private final ResourceLocation texture;

    private ${name}ItemRenderer(Model model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean glint) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferSource, model.renderType(texture), false, glint);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT) public static record Unbaked(int index) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<${name}ItemRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("index").xmap(opt -> opt.orElse(-1), i -> i == -1 ? Optional.empty() : Optional.of(i)).forGetter(${name}ItemRenderer.Unbaked::index)
            ).apply(instance, ${name}ItemRenderer.Unbaked::new)
        );

        @Override
        public MapCodec<${name}ItemRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return ${name}ItemRenderer.MODELS.get(index).apply(modelSet);
        }
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT) public static void registerItemRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(ResourceLocation.parse("${modid}:${registryname}"), ${name}ItemRenderer.Unbaked.MAP_CODEC);
    }
}
</#compress>
<#-- @formatter:on -->