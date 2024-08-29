<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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

/*
 *	MCreator note: This file will be REGENERATED on each build.
 */

package ${package}.init;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ${JavaModName}LootModifiers {

    private class AddTableLootModifier extends LootModifier {
        public static final Supplier<Codec<AddTableLootModifier>> CODEC = Suppliers.memoize(
                () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                        .and(ResourceLocation.CODEC.fieldOf("table").forGetter(m -> m.lootTable))
                        .apply(instance, AddTableLootModifier::new)
                )
        );

        private final ResourceLocation lootTable;

        public AddTableLootModifier(LootItemCondition[] conditions, ResourceLocation lootTable) {
            super(conditions);
            this.lootTable = lootTable;
        }

        @Override
        protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            context.getResolver().getLootTable(lootTable).getRandomItemsRaw(context, generatedLoot::add);
            return generatedLoot;
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ${JavaModName}.MODID);

    public static final RegistryObject<Codec<AddTableLootModifier>> ADD_TABLE_LOOT_MODIFIER_TYPE = REGISTRY.register("add_table", AddTableLootModifier.CODEC);

	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus()));
	}

}

<#-- @formatter:on -->