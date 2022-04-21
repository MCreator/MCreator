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
<#include "mcitems.ftl">

package ${package}.world;

import net.minecraft.util.SoundEvent;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Profession {

    public static final DeferredRegister<PointOfInterestType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, "${modid}");
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, "${modid}");
    public static final RegistryObject<PointOfInterestType> ${data.displayName?upper_case}_POI = POI.register("${data.displayName?lower_case}", () -> new PointOfInterestType("${data.displayName?lower_case}", getAllStates(${mappedBlockToBlock(data.pointOfInterest)}), 1, 1));
    public static final RegistryObject<VillagerProfession> ${data.displayName?upper_case} = registerProfession("${data.displayName?lower_case}", ${name}Profession.${data.displayName?upper_case}_POI);

    @SuppressWarnings("SameParameterValue")
    private static RegistryObject<VillagerProfession> registerProfession(String name, Supplier<PointOfInterestType> poiType) {
        return PROFESSIONS.register(name, () -> new ${JavaModName}VillagerProfessions("${modid}" + ":" + name, poiType.get(), ImmutableSet.of(), ImmutableSet.of(), () -> new SoundEvent(new ResourceLocation("${data.actionSound}"))));
    }

    private static Set<BlockState> getAllStates(Block block) {
        return ImmutableSet.copyOf(block.getStateContainer().getValidStates());
    }

    @SubscribeEvent public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ${JavaModName}VillagerProfessions.fixup(${name}Profession.${data.displayName?upper_case}_POI.get()));
    }

    public static class ${JavaModName}VillagerProfessions extends VillagerProfession {

        private static final Method blockStatesInjector = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class);
        private final List<Supplier<SoundEvent>> soundEventSuppliers;

        @SafeVarargs
        public ${JavaModName}VillagerProfessions(String name, PointOfInterestType pointOfInterest, ImmutableSet<Item> specificItems, ImmutableSet<Block> relatedWorldBlocks, Supplier<SoundEvent>... soundEventSuppliers) {
            super(name, pointOfInterest, specificItems, relatedWorldBlocks, null);
            this.soundEventSuppliers = Arrays.asList(soundEventSuppliers);
        }

        @Nullable
        @Override
        public SoundEvent getSound() {
            int n = ThreadLocalRandom.current().nextInt(soundEventSuppliers.size());
            return soundEventSuppliers.get(n).get();
        }

        public static void fixup(PointOfInterestType poiType) {
            try {
                blockStatesInjector.invoke(null, poiType);
            } catch (IllegalAccessException | InvocationTargetException e) {
                ${JavaModName}.LOGGER.catching(e);
            }
        }
    }
}