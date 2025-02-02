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

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT) public class LegacyOverrideSelectItemModel implements ItemModel {

    private final ModelOverride[] overrides;
    private final ItemModel[] models;
    private final ItemModel fallback;

    private LegacyOverrideSelectItemModel(ModelOverride[] overrides, ItemModel[] models, ItemModel fallback) {
        this.overrides = overrides;
        this.models = models;
        this.fallback = fallback;
    }

    @Override public void update(ItemStackRenderState renderState, ItemStack itemStack, ItemModelResolver modelResolver,
            ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        ItemModel model = fallback;
        for (int i = overrides.length - 1; i >= 0; i--) {
            if (overrides[i].test(itemStack, level, entity, seed, displayContext)) {
                model = models[i];
                break;
            }
        }
        model.update(renderState, itemStack, modelResolver, displayContext, level, entity, seed);
    }

    @OnlyIn(Dist.CLIENT) public record FloatEntry(RangeSelectItemModelProperty property, float value) implements PredicateEntry {
        public static final Codec<LegacyOverrideSelectItemModel.FloatEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                RangeSelectItemModelProperties.MAP_CODEC.fieldOf("property").forGetter(LegacyOverrideSelectItemModel.FloatEntry::property),
                Codec.FLOAT.fieldOf("value").forGetter(LegacyOverrideSelectItemModel.FloatEntry::value)
            ).apply(instance, LegacyOverrideSelectItemModel.FloatEntry::new)
        );

        @Override public boolean test(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
            return property.get(itemStack, level, entity, seed) >= value;
        }
    }

    @OnlyIn(Dist.CLIENT) public record BoolEntry(ConditionalItemModelProperty property, boolean value) implements PredicateEntry {
        public static final Codec<LegacyOverrideSelectItemModel.BoolEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                ConditionalItemModelProperties.MAP_CODEC.fieldOf("property").forGetter(LegacyOverrideSelectItemModel.BoolEntry::property),
                Codec.BOOL.fieldOf("value").forGetter(LegacyOverrideSelectItemModel.BoolEntry::value)
            ).apply(instance, LegacyOverrideSelectItemModel.BoolEntry::new)
        );

        @Override public boolean test(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
            return property.get(itemStack, level, entity, seed, displayContext) || !value;
        }
    }

    @OnlyIn(Dist.CLIENT) public interface PredicateEntry {
        Codec<LegacyOverrideSelectItemModel.PredicateEntry> CODEC = Codec.either(
            LegacyOverrideSelectItemModel.FloatEntry.CODEC,
            LegacyOverrideSelectItemModel.BoolEntry.CODEC
        ).xmap(Either::unwrap, predicate -> {
            if (predicate instanceof LegacyOverrideSelectItemModel.FloatEntry floatPredicate)
                return Either.left(floatPredicate);
            else if (predicate instanceof LegacyOverrideSelectItemModel.BoolEntry boolPredicate)
                return Either.right(boolPredicate);
            throw new UnsupportedOperationException();
        });

        boolean test(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext);
    }

    @OnlyIn(Dist.CLIENT) public record ModelOverride(List<PredicateEntry> predicate, ItemModel.Unbaked model) {
        public static final Codec<LegacyOverrideSelectItemModel.ModelOverride> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                LegacyOverrideSelectItemModel.PredicateEntry.CODEC.listOf().fieldOf("predicate").forGetter(LegacyOverrideSelectItemModel.ModelOverride::predicate),
                ItemModels.CODEC.fieldOf("model").forGetter(LegacyOverrideSelectItemModel.ModelOverride::model)
            ).apply(instance, LegacyOverrideSelectItemModel.ModelOverride::new)
        );

        public boolean test(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
            boolean flag = true;
            for (PredicateEntry entry : predicate)
                flag &= entry.test(itemStack, level, entity, seed, displayContext);
            return flag;
        }
    }

    @OnlyIn(Dist.CLIENT) public record Unbaked(List<LegacyOverrideSelectItemModel.ModelOverride> overrides, ItemModel.Unbaked fallback) implements ItemModel.Unbaked {
        public static final MapCodec<LegacyOverrideSelectItemModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                LegacyOverrideSelectItemModel.ModelOverride.CODEC.listOf().fieldOf("overrides").forGetter(LegacyOverrideSelectItemModel.Unbaked::overrides),
                ItemModels.CODEC.fieldOf("fallback").forGetter(LegacyOverrideSelectItemModel.Unbaked::fallback)
            ).apply(instance, LegacyOverrideSelectItemModel.Unbaked::new)
        );

        @Override public MapCodec<LegacyOverrideSelectItemModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override public ItemModel bake(ItemModel.BakingContext bakingContext) {
            ItemModel[] models = new ItemModel[overrides.size()];
            for (int i = 0; i < overrides.size(); i++)
               models[i] = overrides.get(i).model.bake(bakingContext);
            return new LegacyOverrideSelectItemModel(overrides.toArray(LegacyOverrideSelectItemModel.ModelOverride[]::new), models, fallback.bake(bakingContext));
        }

        @Override public void resolveDependencies(ResolvableModel.Resolver resolver) {
            fallback.resolveDependencies(resolver);
            overrides.forEach(override -> override.model.resolveDependencies(resolver));
        }
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT) public static void registerItemModelTypes(RegisterItemModelsEvent event) {
        event.register(ResourceLocation.parse("${modid}:legacy_overrides"), LegacyOverrideSelectItemModel.Unbaked.MAP_CODEC);
    }

}
<#-- @formatter:on -->